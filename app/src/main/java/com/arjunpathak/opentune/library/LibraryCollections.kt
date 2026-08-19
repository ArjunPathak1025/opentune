package com.arjunpathak.opentune.library

fun List<LocalTrack>.toAlbums(): List<Album> =
    groupBy { "${it.album}\u0000${it.artist}" }
        .map { (key, tracks) ->
            val first = tracks.first()
            Album(key, first.album, first.artist, first.artworkUri, tracks.sortedBy { it.title.lowercase() })
        }
        .sortedBy { it.title.lowercase() }

fun List<LocalTrack>.toArtists(): List<Artist> =
    groupBy { it.artist.ifBlank { "Unknown artist" } }
        .map { (name, tracks) -> Artist(name, tracks.size) }
        .sortedBy { it.name.lowercase() }
