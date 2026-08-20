package com.arjunpathak.opentune.library

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Small device-local playlist store. Track IDs are resolved against the current MediaStore library. */
class PlaylistStore(context: Context) {
    private val prefs = context.getSharedPreferences("opentune_playlists", Context.MODE_PRIVATE)

    fun getPlaylists(): Map<String, List<Long>> = read().mapValues { it.value.toList() }

    fun createPlaylist(name: String): Boolean {
        val clean = name.trim()
        if (clean.isBlank() || read().containsKey(clean)) return false
        val playlists = read().toMutableMap()
        playlists[clean] = mutableListOf()
        write(playlists)
        return true
    }

    fun addTrack(name: String, trackId: Long) {
        val playlists = read().toMutableMap()
        val tracks = playlists[name]?.toMutableList() ?: return
        if (trackId !in tracks) tracks += trackId
        playlists[name] = tracks
        write(playlists)
    }

    fun removeTrack(name: String, trackId: Long) {
        val playlists = read().toMutableMap()
        val tracks = playlists[name]?.toMutableList() ?: return
        tracks.remove(trackId)
        playlists[name] = tracks
        write(playlists)
    }

    private fun read(): Map<String, List<Long>> {
        val root = JSONObject(prefs.getString(KEY, "{}") ?: "{}")
        return root.keys().asSequence().associateWith { key ->
            val array = root.optJSONArray(key) ?: JSONArray()
            List(array.length()) { index -> array.optLong(index) }
        }
    }

    private fun write(playlists: Map<String, List<Long>>) {
        val root = JSONObject()
        playlists.forEach { (name, ids) ->
            val array = JSONArray()
            ids.forEach(array::put)
            root.put(name, array)
        }
        prefs.edit().putString(KEY, root.toString()).apply()
    }

    private companion object { const val KEY = "playlists" }
}
