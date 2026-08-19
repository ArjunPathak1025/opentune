package com.arjunpathak.opentune.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AlbumDetailScreen(
    album: Album,
    onBack: () -> Unit,
    onPlayTrack: (LocalTrack) -> Unit,
    onPlayAll: (List<LocalTrack>) -> Unit
) {
    Column(Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Text("Album", style = MaterialTheme.typography.labelLarge)
        }

        AlbumArtwork(
            artworkUri = album.artworkUri,
            contentDescription = album.title,
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        Text(album.title, style = MaterialTheme.typography.headlineMedium)
        Text(album.artist, style = MaterialTheme.typography.bodyLarge)
        Text("${album.tracks.size} songs", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(14.dp))

        Button(onClick = { onPlayAll(album.tracks) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.PlayArrow, null)
            Text("  Play all")
        }
        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(album.tracks, key = { _, track -> track.id }) { index, track ->
                Row(
                    Modifier.fillMaxWidth().clickable { onPlayTrack(track) }.padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}", modifier = Modifier.size(30.dp), style = MaterialTheme.typography.bodyMedium)
                    Column(Modifier.weight(1f)) {
                        Text(track.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                        Text(track.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    }
                    Icon(Icons.Default.PlayArrow, "Play ${track.title}")
                }
            }
        }
    }
}
