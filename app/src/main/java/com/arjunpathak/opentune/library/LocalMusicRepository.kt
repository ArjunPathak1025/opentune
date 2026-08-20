package com.arjunpathak.opentune.library

import android.content.ContentResolver
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class TrackSortOrder {
    TITLE,
    ARTIST,
    ALBUM,
    RECENTLY_ADDED
}

class LocalMusicRepository(private val contentResolver: ContentResolver) {
    suspend fun getTracks(sortOrder: TrackSortOrder = TrackSortOrder.TITLE): List<LocalTrack> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<LocalTrack>()
        val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val order = when (sortOrder) {
            TrackSortOrder.TITLE -> "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
            TrackSortOrder.ARTIST -> "${MediaStore.Audio.Media.ARTIST} COLLATE NOCASE ASC, ${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
            TrackSortOrder.ALBUM -> "${MediaStore.Audio.Media.ALBUM} COLLATE NOCASE ASC, ${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
            TrackSortOrder.RECENTLY_ADDED -> "${MediaStore.Audio.Media.DATE_ADDED} DESC, ${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
        }

        contentResolver.query(collection, projection, selection, null, order)?.use { cursor ->
            val id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val title = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val album = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val duration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val trackId = cursor.getLong(id)
                val artwork = cursor.getLong(albumId).takeIf { it > 0 }?.let {
                    "content://media/external/audio/albumart/$it"
                }
                tracks += LocalTrack(
                    id = trackId,
                    title = cursor.getString(title).orEmpty(),
                    artist = cursor.getString(artist).orEmpty().ifBlank { "Unknown artist" },
                    album = cursor.getString(album).orEmpty().ifBlank { "Unknown album" },
                    durationMs = cursor.getLong(duration),
                    uri = "${MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}/$trackId",
                    artworkUri = artwork
                )
            }
        }
        tracks
    }
}
