package com.arjunpathak.opentune.library

import android.content.Context

class FavoritesStore(context: Context) {
    private val prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    fun getIds(): List<Long> = prefs.getStringSet(KEY, emptySet())
        ?.mapNotNull { it.toLongOrNull() }
        ?.sorted()
        ?: emptyList()

    fun contains(id: Long): Boolean = getIds().contains(id)

    fun toggle(id: Long): List<Long> {
        val ids = getIds().toMutableSet()
        if (!ids.add(id)) ids.remove(id)
        val saved = ids.map(Long::toString).toSet()
        prefs.edit().putStringSet(KEY, saved).apply()
        return ids.sorted()
    }

    fun clear() = prefs.edit().remove(KEY).apply()

    companion object { private const val KEY = "track_ids" }
}
