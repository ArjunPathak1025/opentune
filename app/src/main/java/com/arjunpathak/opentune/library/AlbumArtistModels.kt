package com.arjunpathak.opentune.library

data class Album(
    val key: String,
    val title: String,
    val artist: String,
    val artworkUri: String?,
    val tracks: List<LocalTrack>
)

data class Artist(
    val name: String,
    val trackCount: Int
)
