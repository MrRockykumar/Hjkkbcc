package com.example.data

import android.content.Context
import android.content.SharedPreferences

class FavoritesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("streamhub_favorites", Context.MODE_PRIVATE)
    private val keyFavorites = "favorite_channel_ids"

    fun getFavoriteIds(): Set<String> {
        return prefs.getStringSet(keyFavorites, emptySet()) ?: emptySet()
    }

    fun addFavorite(channelId: String) {
        val current = getFavoriteIds().toMutableSet()
        current.add(channelId)
        prefs.edit().putStringSet(keyFavorites, current).apply()
    }

    fun removeFavorite(channelId: String) {
        val current = getFavoriteIds().toMutableSet()
        current.remove(channelId)
        prefs.edit().putStringSet(keyFavorites, current).apply()
    }

    fun isFavorite(channelId: String): Boolean {
        return getFavoriteIds().contains(channelId)
    }
}
