package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChannelUiModel
import com.example.ui.components.*
import com.example.ui.viewmodel.StreamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: StreamViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredChannels by viewModel.filteredChannels.collectAsState()
    val featuredChannel by viewModel.featuredChannel.collectAsState()
    val categories by viewModel.categories.collectAsState()

    // Create custom lists of categories for quick navigation
    val categoriesWithAll = listOf("all" to "All Streams") + categories.map { it.id to it.name }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Featured Channel Banner (Only if there is a featured channel and no active search filter, Spans full width)
        if (featuredChannel != null && searchQuery.isEmpty() && selectedCategory == "all") {
            item(span = { GridItemSpan(2) }) {
                FeaturedChannelCard(
                    channelUi = featuredChannel!!,
                    onClick = { viewModel.playChannel(featuredChannel!!) }
                )
            }
        }

        // Category Selection Chips Row (Spans full width)
        item(span = { GridItemSpan(2) }) {
            Column {
                Text(
                    text = "CATEGORIES",
                    color = GlassTheme.TextMuted,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categoriesWithAll) { (catId, catName) ->
                        val isSelected = selectedCategory == catId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) GlassTheme.AccentColor else Color(0x1AFFFFFF)
                                )
                                .clickable { viewModel.selectCategory(catId) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = catName,
                                color = GlassTheme.TextWhite,
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Channels list title (Spans full width)
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp, start = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE NOW",
                    color = GlassTheme.TextWhite,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${filteredChannels.size} Channels",
                    color = GlassTheme.AccentPink,
                    fontSize = 11.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Grid channels (Each occupies 1 span out of 2)
        if (filteredChannels.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FrostedGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Streams Found",
                                color = GlassTheme.TextWhite,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No live channels currently match your search criteria. Try removing filters or changing your query.",
                                color = GlassTheme.TextMuted,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            items(filteredChannels, key = { it.id }) { channel ->
                StandardChannelCard(
                    channelUi = channel,
                    onClick = { viewModel.playChannel(channel) },
                    onFavoriteClick = { viewModel.toggleFavorite(channel.id) }
                )
            }
        }
    }
}
