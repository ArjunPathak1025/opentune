package com.arjunpathak.opentune.model

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val uri: String
)
