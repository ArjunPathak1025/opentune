package com.arjunpathak.opentune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arjunpathak.opentune.ui.theme.OpenTuneTheme

private data class DemoTrack(val title: String, val artist: String, val color: Color)

private val demoTracks = listOf(
    DemoTrack("Midnight Drive", "OpenTune Radio", Color(0xFF5B4B8A)),
    DemoTrack("Neon Skies", "OpenTune Sessions", Color(0xFF176B87)),
    DemoTrack("Afterglow", "OpenTune Selects", Color(0xFF8A4F5B)),
    DemoTrack("Golden Hour", "OpenTune Discover", Color(0xFF8A6B3F))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OpenTuneTheme { OpenTuneApp() } }
    }
}

@Composable
private fun OpenTuneApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentTrack by remember { mutableStateOf(demoTracks.first()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
                NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = { Icon(Icons.Default.LibraryMusic, null) }, label = { Text("Library") })
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                0 -> HomeScreen(currentTrack, isPlaying, onPlay = { currentTrack = it; isPlaying = true })
                1 -> PlaceholderScreen("Search", "Find songs, artists, albums and playlists")
                else -> PlaceholderScreen("Your Library", "Your music, favorites and playlists")
            }
            MiniPlayer(currentTrack, isPlaying, onTogglePlay = { isPlaying = !isPlaying })
        }
    }
}

@Composable
private fun HomeScreen(currentTrack: DemoTrack, isPlaying: Boolean, onPlay: (DemoTrack) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Good evening", style = MaterialTheme.typography.labelLarge)
                Text("Discover", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = {}) { Icon(Icons.Default.FavoriteBorder, "Favorites") }
            IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, "More") }
        }

        Card(Modifier.fillMaxWidth().height(190.dp), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
            Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF6C4AB6), Color(0xFF1F6E8C)))).padding(24.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("OPENTUNE PICKS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Fresh music for your evening", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Text("A mix of discoveries and favorites", color = Color.White.copy(alpha = .8f))
                    Surface(shape = RoundedCornerShape(50), color = Color.White) { Text("Start listening", Modifier.padding(horizontal = 18.dp, vertical = 10.dp), fontWeight = FontWeight.SemiBold) }
                }
            }
        }

        Text("Quick picks", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) { items(demoTracks) { TrackCard(it, onPlay) } }

        Text("Made for you", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        PlaylistRow("Daily Discovery", "A fresh selection based on your listening")
        PlaylistRow("Late Night", "Chill sounds for the last hours of the day")
        PlaylistRow("New Releases", "Explore what is new")
        Text("Currently playing: ${currentTrack.title}", style = MaterialTheme.typography.labelMedium)
        if (isPlaying) Text("Playing", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TrackCard(track: DemoTrack, onPlay: (DemoTrack) -> Unit) {
    Column(Modifier.width(150.dp).clickable { onPlay(track) }) {
        Box(Modifier.size(150.dp).clip(RoundedCornerShape(20.dp)).background(track.color)) {
            Text("♪", Modifier.align(Alignment.Center), color = Color.White, fontSize = 52.sp)
            Surface(Modifier.align(Alignment.BottomEnd).padding(8.dp), shape = RoundedCornerShape(50), color = Color.White) { Icon(Icons.Default.PlayArrow, "Play", Modifier.padding(7.dp).size(20.dp)) }
        }
        Spacer(Modifier.height(8.dp))
        Text(track.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
        Text(track.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}

@Composable
private fun PlaylistRow(title: String, subtitle: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) { Text("♫", fontSize = 25.sp) }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, style = MaterialTheme.typography.bodySmall) }
        IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, "More") }
    }
}

@Composable
private fun MiniPlayer(track: DemoTrack, isPlaying: Boolean, onTogglePlay: () -> Unit) {
    Surface(shadowElevation = 8.dp) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(track.color), contentAlignment = Alignment.Center) { Text("♪", color = Color.White, fontSize = 22.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(track.title, fontWeight = FontWeight.SemiBold, maxLines = 1); Text(track.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1) }
            IconButton(onClick = onTogglePlay) { Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "Play or pause") }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, subtitle: String) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyLarge)
    }
}
