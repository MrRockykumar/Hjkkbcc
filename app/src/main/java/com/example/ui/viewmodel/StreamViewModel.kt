package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface UiState {
    object Loading : UiState
    data class Success(val message: String) : UiState
    data class Error(val error: String) : UiState
}

class StreamViewModel(application: Application) : AndroidViewModel(application) {

    private val api = IptvApi.create()
    private val favoritesManager = FavoritesManager(application)

    // State holders
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _rawChannels = MutableStateFlow<List<Channel>>(emptyList())
    private val _rawStreams = MutableStateFlow<List<Stream>>(emptyList())
    private val _rawLogos = MutableStateFlow<List<Logo>>(emptyList())
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries.asStateFlow()

    // Master list of resolved, playable Channel UI Models
    private val _masterChannels = MutableStateFlow<List<ChannelUiModel>>(emptyList())

    // Filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedCountry = MutableStateFlow("all")
    val selectedCountry: StateFlow<String> = _selectedCountry.asStateFlow()

    // Favorites ID set
    private val _favoriteIds = MutableStateFlow<Set<String>>(favoritesManager.getFavoriteIds())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    // Player state
    private val _currentPlayingChannel = MutableStateFlow<ChannelUiModel?>(null)
    val currentPlayingChannel: StateFlow<ChannelUiModel?> = _currentPlayingChannel.asStateFlow()

    // Featured Channel (Netflix discovery banner at the top)
    private val _featuredChannel = MutableStateFlow<ChannelUiModel?>(null)
    val featuredChannel: StateFlow<ChannelUiModel?> = _featuredChannel.asStateFlow()

    // Active bottom navigation tab: "explore", "countries", "favorites", "settings"
    private val _activeTab = MutableStateFlow("explore")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    // Main filtered channels list displayed on the screen
    val filteredChannels: StateFlow<List<ChannelUiModel>> = combine(
        _masterChannels,
        _searchQuery,
        _selectedCategory,
        _selectedCountry,
        _favoriteIds,
        _activeTab
    ) { flowArray ->
        @Suppress("UNCHECKED_CAST")
        val channels = flowArray[0] as List<ChannelUiModel>
        val query = flowArray[1] as String
        val category = flowArray[2] as String
        val country = flowArray[3] as String
        @Suppress("UNCHECKED_CAST")
        val favIds = flowArray[4] as Set<String>
        val tab = flowArray[5] as String

        var result = channels.map { it.copy(isFavorite = favIds.contains(it.id)) }

        // Tab-specific basic filtering
        if (tab == "favorites") {
            result = result.filter { it.isFavorite }
        }

        // Category filter
        if (category != "all" && tab == "explore") {
            result = result.filter { uiModel ->
                uiModel.channel.categories?.contains(category) == true
            }
        }

        // Country filter
        if (country != "all") {
            result = result.filter { uiModel ->
                uiModel.channel.countryCode?.equals(country, ignoreCase = true) == true
            }
        }

        // Search query filter
        if (query.isNotBlank()) {
            result = result.filter { uiModel ->
                uiModel.name.contains(query, ignoreCase = true) ||
                uiModel.channel.altNames?.any { it.contains(query, ignoreCase = true) } == true ||
                uiModel.categoryName.contains(query, ignoreCase = true) ||
                uiModel.countryName.contains(query, ignoreCase = true)
            }
        }

        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            try {
                // Fetch in parallel using async or standard sequential calls with try-catch
                val countriesList = runCatching { api.getCountries() }.getOrDefault(emptyList())
                val categoriesList = runCatching { api.getCategories() }.getOrDefault(emptyList())
                val channelsList = runCatching { api.getChannels() }.getOrDefault(emptyList())
                val streamsList = runCatching { api.getStreams() }.getOrDefault(emptyList())
                val logosList = runCatching { api.getLogos() }.getOrDefault(emptyList())

                if (channelsList.isEmpty() || streamsList.isEmpty()) {
                    _uiState.value = UiState.Error("Could not retrieve channel or stream listings.")
                    return@launch
                }

                _rawChannels.value = channelsList
                _rawStreams.value = streamsList
                _rawLogos.value = logosList
                _categories.value = categoriesList
                _countries.value = countriesList

                // Pre-process relationships on a background thread
                processData(channelsList, streamsList, logosList, categoriesList, countriesList)

                _uiState.value = UiState.Success("Data loaded successfully!")
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown network error occurred.")
            }
        }
    }

    private fun processData(
        channels: List<Channel>,
        streams: List<Stream>,
        logos: List<Logo>,
        categories: List<Category>,
        countries: List<Country>
    ) {
        // Group and filter streams: only keep best stream for each channel
        // Key is channelId
        val bestStreams = streams
            .filter { it.url.isNotBlank() }
            .groupBy { it.channelId }
            .mapValues { (_, streamList) ->
                streamList.sortedWith(
                    compareByDescending<Stream> { stream ->
                        // Prefer higher resolution
                        when (stream.quality?.lowercase()) {
                            "4k", "2160p" -> 4
                            "1080p", "fhd" -> 3
                            "720p", "hd" -> 2
                            "480p", "sd" -> 1
                            else -> 0
                        }
                    }.thenBy { stream ->
                        // Prefer streams without referrer restriction
                        stream.referrer == null || stream.referrer.isBlank()
                    }
                ).firstOrNull()
            }

        // Map logos by channelId
        val logoMap = logos.groupBy { it.channelId }.mapValues { it.value.firstOrNull()?.url }

        // Map categories by ID
        val categoryMap = categories.associateBy { it.id }

        // Map countries by code
        val countryMap = countries.associateBy { it.code.lowercase() }

        // Build list of active ChannelUiModel (only channels with a corresponding stream)
        val resolvedChannels = channels
            .filter { !it.isNsfw && bestStreams.containsKey(it.id) }
            .map { channel ->
                val stream = bestStreams[channel.id]
                val logoUrl = logoMap[channel.id]
                
                val primaryCategoryId = channel.categories?.firstOrNull() ?: ""
                val categoryName = categoryMap[primaryCategoryId]?.name ?: primaryCategoryId.replaceFirstChar { it.uppercase() }

                val countryCode = channel.countryCode?.lowercase() ?: ""
                val country = countryMap[countryCode]

                ChannelUiModel(
                    channel = channel,
                    stream = stream,
                    logoUrl = logoUrl,
                    categoryName = categoryName.ifBlank { "General" },
                    country = country,
                    isFavorite = _favoriteIds.value.contains(channel.id)
                )
            }

        _masterChannels.value = resolvedChannels

        // Pick a premium Featured Channel (Netflix layout banner)
        // Prefer English/US/UK or high-quality channels if possible, or just a prominent random one
        val featured = resolvedChannels.firstOrNull { 
            it.logoUrl != null && 
            (it.channel.countryCode == "US" || it.channel.countryCode == "UK" || it.channel.countryCode == "GB")
        } ?: resolvedChannels.firstOrNull()

        _featuredChannel.value = featured
    }

    // Setters
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: String) {
        _selectedCategory.value = categoryId
    }

    fun selectCountry(countryCode: String) {
        _selectedCountry.value = countryCode
    }

    fun selectTab(tab: String) {
        _activeTab.value = tab
    }

    fun toggleFavorite(channelId: String) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(channelId)) {
            current.remove(channelId)
            favoritesManager.removeFavorite(channelId)
        } else {
            current.add(channelId)
            favoritesManager.addFavorite(channelId)
        }
        _favoriteIds.value = current
    }

    fun playChannel(channel: ChannelUiModel) {
        _currentPlayingChannel.value = channel
    }

    fun stopPlayback() {
        _currentPlayingChannel.value = null
    }
}
