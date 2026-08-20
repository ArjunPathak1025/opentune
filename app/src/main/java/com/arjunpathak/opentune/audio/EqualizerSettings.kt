package com.arjunpathak.opentune.audio

import android.content.Context

class EqualizerSettings(context: Context) {
    private val prefs = context.getSharedPreferences("opentune_eq", Context.MODE_PRIVATE)

    fun preset(): String = prefs.getString("preset", EqualizerPreset.FLAT.name) ?: EqualizerPreset.FLAT.name
    fun setPreset(value: EqualizerPreset) { prefs.edit().putString("preset", value.name).apply() }

    fun band(index: Int): Float = prefs.getFloat("band_$index", 0f)
    fun setBand(index: Int, value: Float) { prefs.edit().putFloat("band_$index", value).apply() }

    fun bass(): Float = prefs.getFloat("bass", 0f)
    fun setBass(value: Float) { prefs.edit().putFloat("bass", value).apply() }

    fun virtualizer(): Float = prefs.getFloat("virtualizer", 0f)
    fun setVirtualizer(value: Float) { prefs.edit().putFloat("virtualizer", value).apply() }
}
