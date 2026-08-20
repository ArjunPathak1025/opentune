package com.arjunpathak.opentune.library

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaylistsScreen(
    tracks: List<LocalTrack>,
    store: PlaylistStore,
    onPlay: (LocalTrack) -> Unit,
    onPlayAll: (List<LocalTrack>) -> Unit
) {
    var playlists by remember { mutableStateOf(store.getPlaylists()) }
    var selectedName by remember { mutableStateOf<String?>(null) }
    var showCreate by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    val selected = selectedName?.let { name -> playlists[name]?.mapNotNull { id -> tracks.firstOrNull { it.id == id } } }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Playlists", style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = { showCreate = true }) { Icon(Icons.Default.Add, "Create playlist") }
        }
        Spacer(Modifier.height(16.dp))
        if (selectedName == null) {
            if (playlists.isEmpty()) Text("Create your first playlist.", style = MaterialTheme.typography.bodyLarge)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(playlists.keys.toList()) { name ->
                    Row(Modifier.fillMaxWidth().clickable { selectedName = name }.padding(14.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(name, style = MaterialTheme.typography.titleMedium)
                            Text("${playlists[name]?.size ?: 0} songs", style = MaterialTheme.typography.bodySmall)
                        }
                        Icon(Icons.Default.PlayArrow, "Open playlist")
                    }
                }
            }
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(selectedName!!, style = MaterialTheme.typography.titleLarge)
                    Text("${selected?.size ?: 0} songs", style = MaterialTheme.typography.bodySmall)
                }
                Button(onClick = { selected?.let(onPlayAll) }) { Text("Play all") }
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(selected.orEmpty()) { track ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Column(Modifier.weight(1f).clickable { onPlay(track) }) {
                            Text(track.title, style = MaterialTheme.typography.titleSmall)
                            Text(track.artist, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = {
                            store.removeTrack(selectedName!!, track.id)
                            playlists = store.getPlaylists()
                        }) { Icon(Icons.Default.Delete, "Remove from playlist") }
                    }
                }
            }
        }
    }

    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text("New playlist") },
            text = { OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Playlist name") }, singleLine = true) },
            confirmButton = {
                Button(onClick = {
                    if (store.createPlaylist(newName)) {
                        playlists = store.getPlaylists()
                        newName = ""
                        showCreate = false
                    }
                }) { Text("Create") }
            },
            dismissButton = { Button(onClick = { showCreate = false }) { Text("Cancel") } }
        )
    }
}
