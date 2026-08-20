package com.arjunpathak.opentune.library

import android.content.Context

class HistoryStore(context: Context) {
    private val prefs = context.getSharedPreferences("opentune_history", Context.MODE_PRIVATE)

    fun getIds(): List<Long> = prefs.getString(KEY, "")
        .orEmpty()
        .split(',')
        .mapNotNull { it.toLongOrNull() }

    fun record(trackId: Long) {
        val updated = listOf(trackId) + getIds().filterNot { it == trackId }
        prefs.edit().putString(KEY, updated.take(MAX_ITEMS).joinToString(",")).apply()
    }

    fun clear() = prefs.edit().remove(KEY).apply()

    private companion object {
        const val KEY = "recent_track_ids"
        const val MAX_ITEMS = 50
    }
}
