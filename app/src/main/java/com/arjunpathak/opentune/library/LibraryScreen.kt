package com.arjunpathak.opentune.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LocalLibraryScreen(
    onTrackClick: (LocalTrack) -> Unit,
    onPlayAlbum: (List<LocalTrack>) -> Unit = { tracks -> tracks.firstOrNull()?.let(onTrackClick) },
    viewModel: LibraryViewModel = viewModel()
) {
    val tracks by viewModel.tracks.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    var selectedArtist by remember { mutableStateOf<Artist?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.refresh() }

    if (selectedAlbum != null) {
        AlbumDetailScreen(selectedAlbum!!, { selectedAlbum = null }, onTrackClick, onPlayAlbum)
        return
    }
    if (selectedArtist != null) {
        ArtistDetailScreen(selectedArtist!!, { selectedArtist = null }, onTrackClick, onPlayAlbum)
        return
    }

    val filteredTracks = remember(tracks, searchQuery) {
        val query = searchQuery.trim().lowercase()
        if (query.isBlank()) tracks else tracks.filter {
            it.title.lowercase().contains(query) ||
                it.artist.lowercase().contains(query) ||
                it.album.lowercase().contains(query)
        }
    }

    val sortLabel = when (sortOrder) {
        TrackSortOrder.TITLE -> "Title"
        TrackSortOrder.ARTIST -> "Artist"
        TrackSortOrder.ALBUM -> "Album"
        TrackSortOrder.RECENTLY_ADDED -> "Recently added"
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Your Library", style = MaterialTheme.typography.headlineMedium)
                Text("${tracks.size} songs on this device", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = viewModel::refresh) { Icon(Icons.Default.Refresh, "Refresh library") }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, "Search library") },
            placeholder = { Text("Search songs, artists, albums") },
            shape = RoundedCornerShape(16.dp)
        )

        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sort: $sortLabel", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            IconButton(onClick = { sortMenuExpanded = true }) {
                Icon(Icons.Default.Sort, "Sort library")
                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Title") }, onClick = {
                        sortMenuExpanded = false
                        viewModel.setSortOrder(TrackSortOrder.TITLE)
                    })
                    DropdownMenuItem(text = { Text("Artist") }, onClick = {
                        sortMenuExpanded = false
                        viewModel.setSortOrder(TrackSortOrder.ARTIST)
                    })
                    DropdownMenuItem(text = { Text("Album") }, onClick = {
                        sortMenuExpanded = false
                        viewModel.setSortOrder(TrackSortOrder.ALBUM)
                    })
                    DropdownMenuItem(text = { Text("Recently added") }, onClick = {
                        sortMenuExpanded = false
                        viewModel.setSortOrder(TrackSortOrder.RECENTLY_ADDED)
                    })
                }
            }
        }

        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Songs") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Albums") })
            Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Artists") })
        }

        if (loading) {
            Spacer(Modifier.size(24.dp))
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
        } else if (tracks.isEmpty()) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.MusicNote, null, Modifier.size(64.dp), tint = Color.Gray)
                Text("No music found", style = MaterialTheme.typography.titleLarge)
                Text("Add music to your device and refresh.")
            }
        } else if (filteredTracks.isEmpty()) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.Search, null, Modifier.size(56.dp), tint = Color.Gray)
                Text("No matches", style = MaterialTheme.typography.titleLarge)
                Text("Try another song, artist, or album name.")
            }
        } else if (tab == 0) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 18.dp)) {
                items(filteredTracks, key = { it.id }) { track ->
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        AlbumArtwork(track.artworkUri, track.album, Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)))
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(track.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                            Text("${track.artist} • ${track.album}", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                        }
                        IconButton(onClick = { onTrackClick(track) }) { Icon(Icons.Default.MusicNote, "Play") }
                    }
                }
            }
        } else {
            AlbumsArtistsScreen(
                tracks = filteredTracks,
                onTrackClick = onTrackClick,
                onAlbumClick = { selectedAlbum = it },
                onArtistClick = { selectedArtist = it }
            )
        }
    }
}
