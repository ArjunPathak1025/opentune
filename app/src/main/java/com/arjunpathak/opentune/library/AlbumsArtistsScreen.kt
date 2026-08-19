package com.arjunpathak.opentune.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AlbumsArtistsScreen(
    tracks: List<LocalTrack>,
    onTrackClick: (LocalTrack) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val albums = remember(tracks) { tracks.toAlbums() }
    val artists = remember(tracks) { tracks.toArtists() }

    Column(Modifier.fillMaxSize()) {
        Text("Albums & Artists", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(20.dp))
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Albums") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Artists") })
        }
        if (tab == 0) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(albums, key = { it.key }) { album -> AlbumCard(album) { onAlbumClick(album) } }
            }
        } else {
            LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(artists, key = { it.name }) { artist ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onArtistClick(artist) }.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AlbumArtwork(artist.artworkUri, artist.name, Modifier.size(58.dp).clip(RoundedCornerShape(16.dp)))
                        Column(Modifier.padding(start = 14.dp)) {
                            Text(artist.name, style = MaterialTheme.typography.titleMedium)
                            Text("${artist.trackCount} songs", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumCard(album: Album, onClick: () -> Unit) {
    Column(Modifier.clickable(onClick = onClick)) {
        AlbumArtwork(album.artworkUri, album.title, Modifier.fillMaxWidth().height(165.dp).clip(RoundedCornerShape(18.dp)))
        Spacer(Modifier.height(8.dp))
        Text(album.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
        Text(album.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}
