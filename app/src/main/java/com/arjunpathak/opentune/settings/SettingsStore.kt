package com.arjunpathak.opentune.settings

import android.content.Context

class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("opentune_settings", Context.MODE_PRIVATE)

    fun isDarkMode(): Boolean = prefs.getBoolean("dark_mode", false)
    fun setDarkMode(enabled: Boolean) { prefs.edit().putBoolean("dark_mode", enabled).apply() }

    fun getPlaybackQuality(): String = prefs.getString("quality", "High") ?: "High"
    fun setPlaybackQuality(value: String) { prefs.edit().putString("quality", value).apply() }
}
