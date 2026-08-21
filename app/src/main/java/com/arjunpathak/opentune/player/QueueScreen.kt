package com.arjunpathak.opentune.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arjunpathak.opentune.library.AlbumArtwork
import com.arjunpathak.opentune.model.Track

@Composable
fun QueueScreen(
    player: PlayerController,
    onBack: () -> Unit,
    onPlayingChanged: (Track?) -> Unit
) {
    var queue by remember { mutableStateOf<List<Track>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(-1) }
    var confirmClear by remember { mutableStateOf(false) }

    fun refresh() = player.queue { items, index -> queue = items; currentIndex = index; onPlayingChanged(items.getOrNull(index)) }
    LaunchedEffect(Unit) { refresh() }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Up Next", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            if (queue.isNotEmpty()) {
                IconButton(onClick = { confirmClear = true }) { Icon(Icons.Default.ClearAll, "Clear queue") }
            }
            Text("Done", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { onBack() }.padding(8.dp))
        }

        if (queue.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.PlayArrow, null, Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.size(16.dp))
                Text("Your queue is empty", style = MaterialTheme.typography.titleLarge)
                Text("Play an album or playlist to fill Up Next.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn {
                itemsIndexed(queue, key = { _, track -> track.id }) { index, track ->
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DragHandle, "Reorder", Modifier.size(22.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        AlbumArtwork(track.artworkUri, track.album ?: track.title, Modifier.size(52.dp))
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp).clickable { player.playQueueItem(index); refresh() }) {
                            Text(track.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                            Text(if (index == currentIndex) "Playing • ${track.artist}" else track.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                        }
                        IconButton(onClick = { player.removeQueueItem(index); refresh() }) { Icon(Icons.Default.RemoveCircleOutline, "Remove") }
                    }
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear queue?") },
            text = { Text("Remove all songs from Up Next?") },
            confirmButton = { Button(onClick = { player.clearQueue(); confirmClear = false; refresh() }) { Text("Clear") } },
            dismissButton = { Button(onClick = { confirmClear = false }) { Text("Cancel") } }
        )
    }
}
