package com.arjunpathak.opentune.youtube

import android.content.Context

class YouTubeMusicSearchHistory(context: Context) {
    private val prefs = context.getSharedPreferences("youtube_music_search", Context.MODE_PRIVATE)

    fun get(): List<String> = prefs.getStringSet(KEY, emptySet())
        .orEmpty()
        .toList()
        .sortedByDescending { prefs.getLong("time_$it", 0L) }
        .take(MAX_ITEMS)

    fun add(query: String): List<String> {
        val clean = query.trim()
        if (clean.isBlank()) return get()
        val updated = (get().filterNot { it.equals(clean, ignoreCase = true) } + clean).takeLast(MAX_ITEMS)
        prefs.edit()
            .putStringSet(KEY, updated.toSet())
            .putLong("time_$clean", System.currentTimeMillis())
            .apply()
        return updated.asReversed()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY = "queries"
        const val MAX_ITEMS = 8
    }
}
