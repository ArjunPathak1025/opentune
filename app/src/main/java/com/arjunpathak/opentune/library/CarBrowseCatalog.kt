package com.arjunpathak.opentune.library

/** Stable IDs used by the Android Auto / Automotive media browse tree. */
object CarBrowseCatalog {
    const val ROOT = "opentune:root"
    const val SONGS = "opentune:songs"
    const val ALBUMS = "opentune:albums"
    const val ARTISTS = "opentune:artists"
    const val FAVORITES = "opentune:favorites"
    const val HISTORY = "opentune:history"

    fun albumId(album: Album): String = "opentune:album:${album.key}"
    fun artistId(artist: Artist): String = "opentune:artist:${artist.name}"
    fun trackId(track: LocalTrack): String = "opentune:track:${track.id}"
}

fun List<LocalTrack>.carBrowseAlbums(): List<Album> = toAlbums()
fun List<LocalTrack>.carBrowseArtists(): List<Artist> = toArtists()
