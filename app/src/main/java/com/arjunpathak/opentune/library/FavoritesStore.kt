package com.arjunpathak.opentune.library

import android.content.Context

class FavoritesStore(context: Context) {
    private val prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    fun getIds(): Set<Long> = prefs.getStringSet(KEY, emptySet())
        ?.mapNotNull { it.toLongOrNull() }
        ?.toSet()
        ?: emptySet()

    fun contains(id: Long): Boolean = getIds().contains(id)

    fun toggle(id: Long): Set<Long> {
        val ids = getIds().toMutableSet()
        if (!ids.add(id)) ids.remove(id)
        prefs.edit().putStringSet(KEY, ids.map(Long::toString).toSet()).apply()
        return ids
    }

    companion object { private const val KEY = "track_ids" }
}
