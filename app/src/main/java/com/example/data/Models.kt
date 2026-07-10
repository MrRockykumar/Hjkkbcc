package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Channel(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "alt_names") val altNames: List<String>? = null,
    @Json(name = "network") val network: String? = null,
    @Json(name = "owners") val owners: List<String>? = null,
    @Json(name = "country") val countryCode: String? = null,
    @Json(name = "categories") val categories: List<String>? = null,
    @Json(name = "is_nsfw") val isNsfw: Boolean = false,
    @Json(name = "launched") val launched: String? = null,
    @Json(name = "closed") val closed: String? = null,
    @Json(name = "website") val website: String? = null
)

@JsonClass(generateAdapter = true)
data class Stream(
    @Json(name = "channel") val channelId: String,
    @Json(name = "feed") val feed: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "url") val url: String,
    @Json(name = "quality") val quality: String? = null,
    @Json(name = "referrer") val referrer: String? = null,
    @Json(name = "user_agent") val userAgent: String? = null
)

@JsonClass(generateAdapter = true)
data class Logo(
    @Json(name = "channel") val channelId: String,
    @Json(name = "url") val url: String
)

@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String? = null
)

@JsonClass(generateAdapter = true)
data class Country(
    @Json(name = "name") val name: String,
    @Json(name = "code") val code: String,
    @Json(name = "languages") val languages: List<String>? = null,
    @Json(name = "flag") val flag: String? = null
)

// UI wrapper that holds all information resolved for a channel
data class ChannelUiModel(
    val channel: Channel,
    val stream: Stream?,
    val logoUrl: String?,
    val categoryName: String,
    val country: Country?,
    val isFavorite: Boolean = false
) {
    val id: String get() = channel.id
    val name: String get() = channel.name
    val countryFlag: String get() = country?.flag ?: "🌐"
    val countryName: String get() = country?.name ?: "Global"
}
