package com.arjunpathak.opentune.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchScreen(
    tracks: List<LocalTrack>,
    favoriteIds: Set<Long>,
    onTrackClick: (LocalTrack) -> Unit,
    onToggleFavorite: (LocalTrack) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val normalized = query.trim().lowercase()
    val results = remember(tracks, normalized) {
        if (normalized.isBlank()) emptyList()
        else tracks.filter {
            it.title.lowercase().contains(normalized) ||
                it.artist.lowercase().contains(normalized) ||
                it.album.lowercase().contains(normalized)
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Search", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.size(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = { Text("Songs, artists or albums") }
        )
        Spacer(Modifier.size(16.dp))
        if (normalized.isBlank()) {
            Text("Search your device music library", style = MaterialTheme.typography.bodyLarge)
        } else if (results.isEmpty()) {
            Text("No matches found", style = MaterialTheme.typography.titleMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(results, key = { it.id }) { track ->
                    LocalTrackRow(track, favoriteIds.contains(track.id), onTrackClick, onToggleFavorite)
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    tracks: List<LocalTrack>,
    favoriteIds: Set<Long>,
    onTrackClick: (LocalTrack) -> Unit,
    onToggleFavorite: (LocalTrack) -> Unit
) {
    val favorites = remember(tracks, favoriteIds) { tracks.filter { favoriteIds.contains(it.id) } }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Favorites", style = MaterialTheme.typography.headlineMedium)
        Text("${favorites.size} saved songs", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
        if (favorites.isEmpty()) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.FavoriteBorder, null, Modifier.size(64.dp))
                Text("No favorites yet", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
                Text("Tap the heart on any local song to save it.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(favorites, key = { it.id }) { track ->
                    LocalTrackRow(track, true, onTrackClick, onToggleFavorite)
                }
            }
        }
    }
}

@Composable
private fun LocalTrackRow(
    track: LocalTrack,
    favorite: Boolean,
    onTrackClick: (LocalTrack) -> Unit,
    onToggleFavorite: (LocalTrack) -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        AlbumArtwork(track.artworkUri, track.album, Modifier.size(52.dp))
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(track.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Text("${track.artist} • ${track.album}", style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
        IconButton(onClick = { onToggleFavorite(track) }) {
            Icon(if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "Favorite")
        }
        IconButton(onClick = { onTrackClick(track) }) {
            Icon(Icons.Default.Search, "Play")
        }
    }
}
