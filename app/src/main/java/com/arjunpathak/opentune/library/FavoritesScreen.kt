package com.arjunpathak.opentune.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun FavoritesScreen(tracks: List<LocalTrack>, favoriteIds: List<Long>, onTrackClick: (LocalTrack) -> Unit, onToggleFavorite: (LocalTrack) -> Unit) {
    val favorites = favoriteIds.mapNotNull { id -> tracks.firstOrNull { it.id == id } }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Favorites", style = MaterialTheme.typography.headlineMedium); Text("${favorites.size} saved ${if (favorites.size == 1) "song" else "songs"}", style = MaterialTheme.typography.bodyMedium) }
            Icon(Icons.Default.Favorite, null, Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
        }
        if (favorites.isEmpty()) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Icon(Icons.Default.FavoriteBorder, null, Modifier.size(64.dp)); Text("No favorites yet", style = MaterialTheme.typography.titleLarge); Text("Tap the heart on a song to save it here.") }
        } else {
            Button(onClick = { favorites.firstOrNull()?.let(onTrackClick) }, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) { Text("Play Favorites") }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(favorites, key = { it.id }) { track ->
                    Card(onClick = { onTrackClick(track) }, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            AlbumArtwork(track.artworkUri, track.album, Modifier.size(58.dp).clip(RoundedCornerShape(12.dp)))
                            Column(Modifier.weight(1f).padding(start = 12.dp)) { Text(track.title, style = MaterialTheme.typography.titleMedium, maxLines = 1); Text("${track.artist} • ${track.album}", style = MaterialTheme.typography.bodySmall, maxLines = 1) }
                            IconButton(onClick = { onToggleFavorite(track) }) { Icon(Icons.Default.Favorite, "Remove from favorites") }
                        }
                    }
                }
            }
        }
    }
}
