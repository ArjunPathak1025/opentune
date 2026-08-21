package com.arjunpathak.opentune.youtube

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

/** Lightweight Piped fallback provider. It is kept separate from the official YouTube Music handoff. */
class PipedMusicProvider(
    private val apiBase: String = "https://pipedapi.kavin.rocks"
) {
    data class Result(
        val id: String,
        val title: String,
        val artist: String,
        val durationSeconds: Long,
        val thumbnail: String?,
        val url: String?
    )

    suspend fun search(query: String): Result<List<Result>> = withContext(Dispatchers.IO) {
        runCatching {
            val encoded = URLEncoder.encode(query.trim(), Charsets.UTF_8.name())
            val connection = (URL("$apiBase/search?q=$encoded&filter=music_songs").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8_000
                readTimeout = 12_000
                setRequestProperty("Accept", "application/json")
            }
            connection.use { response ->
                if (response.responseCode !in 200..299) error("Piped HTTP ${response.responseCode}")
                val json = response.inputStream.bufferedReader().use { it.readText() }
                val items = JSONObject(json).optJSONArray("items") ?: return@withContext Result.success(emptyList())
                buildList {
                    for (i in 0 until items.length()) {
                        val item = items.optJSONObject(i) ?: continue
                        val type = item.optString("type")
                        if (type != "stream" && type != "musicSong") continue
                        add(Result(
                            id = item.optString("url").ifBlank { item.optString("id") },
                            title = item.optString("title").ifBlank { "Unknown title" },
                            artist = item.optString("uploaderName").ifBlank { item.optString("artist") },
                            durationSeconds = item.optLong("duration", 0L),
                            thumbnail = item.optString("thumbnail").takeIf { it.isNotBlank() },
                            url = item.optString("url").takeIf { it.isNotBlank() }
                        ))
                    }
                }.let { Result.success(it) }
            }
        }.getOrElse { Result.failure(it) }
    }
}
