package com.arjunpathak.opentune.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.arjunpathak.opentune.library.AlbumArtwork
import com.arjunpathak.opentune.model.Track
import com.arjunpathak.opentune.player.PlayerController
import kotlinx.coroutines.delay

@Composable
fun QueueScreen(
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember(context) { PlayerController(context) }
    var items by remember { mutableStateOf<List<Track>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(-1) }

    fun refresh() {
        player.queue { queue, index ->
            items = queue
            currentIndex = index
        }
    }

    LaunchedEffect(Unit) {
        refresh()
        while (true) {
            delay(750)
            refresh()
        }
    }

    DisposableEffect(player) {
        onDispose { player.release() }
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Column(Modifier.weight(1f)) {
                Text("Queue", style = MaterialTheme.typography.headlineSmall)
                Text("${items.size} ${if (items.size == 1) "song" else "songs"}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { player.clearQueue(); refresh() }, enabled = items.isNotEmpty()) {
                Icon(Icons.Default.ClearAll, "Clear queue")
            }
        }

        if (items.isEmpty()) {
            Column(
                Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.MusicNote, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.size(16.dp))
                Text("Your queue is empty", style = MaterialTheme.typography.titleLarge)
                Text("Play an album or a group of songs to fill the queue.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(items, key = { _, track -> track.id + track.uri }) { index, track ->
                    val isCurrent = index == currentIndex
                    Row(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { player.playQueueItem(index); refresh() }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AlbumArtwork(track.artworkUri, track.title, Modifier.size(54.dp).clip(RoundedCornerShape(10.dp)))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                track.title.ifBlank { "Unknown title" },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                listOf(track.artist, track.album).filter { it.isNotBlank() }.joinToString(" • "),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                        }
                        IconButton(onClick = { if (index > 0) { player.moveQueueItem(index, index - 1); refresh() } }, enabled = index > 0) {
                            Icon(Icons.Default.ArrowUpward, "Move up")
                        }
                        IconButton(onClick = { if (index < items.lastIndex) { player.moveQueueItem(index, index + 1); refresh() } }, enabled = index < items.lastIndex) {
                            Icon(Icons.Default.ArrowDownward, "Move down")
                        }
                        IconButton(onClick = { player.removeQueueItem(index); refresh() }) {
                            Icon(Icons.Default.Close, "Remove from queue")
                        }
                    }
                }
            }
        }
    }
}
