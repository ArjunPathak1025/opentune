package com.arjunpathak.opentune.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecentlyPlayedScreen(
    tracks: List<LocalTrack>,
    history: HistoryStore,
    onTrackClick: (LocalTrack) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Recently Played", style = MaterialTheme.typography.headlineMedium)
                Text("Your latest 50 plays", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = history::clear, enabled = tracks.isNotEmpty()) {
                Icon(Icons.Default.DeleteSweep, "Clear history")
            }
        }

        if (tracks.isEmpty()) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.History, null)
                Text("Nothing played yet", style = MaterialTheme.typography.titleLarge)
                Text("Songs you play will appear here.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(tracks, key = { it.id }) { track ->
                    TrackRow(
                        track = track,
                        onClick = { onTrackClick(track) }
                    )
                }
            }
        }
    }
}
