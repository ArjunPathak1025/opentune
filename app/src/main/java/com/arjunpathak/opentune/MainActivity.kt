package com.arjunpathak.opentune

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.core.content.ContextCompat
import com.arjunpathak.opentune.library.LocalLibraryScreen
import com.arjunpathak.opentune.library.LocalTrack
import com.arjunpathak.opentune.model.Track
import com.arjunpathak.opentune.player.PlayerController
import com.arjunpathak.opentune.ui.NowPlayingScreen
import com.arjunpathak.opentune.ui.theme.OpenTuneTheme

private data class DemoTrack(val track: Track, val color: Color)

private val demoTracks = listOf(
    DemoTrack(Track("demo-midnight", "Midnight Drive", "OpenTune Radio", uri = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"), Color(0xFF5B4B8A)),
    DemoTrack(Track("demo-neon", "Neon Skies", "OpenTune Sessions", uri = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"), Color(0xFF176B87)),
    DemoTrack(Track("demo-afterglow", "Afterglow", "OpenTune Selects", uri = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"), Color(0xFF8A4F5B)),
    DemoTrack(Track("demo-golden", "Golden Hour", "OpenTune Discover", uri = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"), Color(0xFF8A6B3F))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OpenTuneTheme { PermissionGate { OpenTuneApp() } } }
    }
}

private fun musicPermission(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO
    else Manifest.permission.READ_EXTERNAL_STORAGE

@Composable
private fun PermissionGate(content: @Composable () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var granted by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, musicPermission()) == PackageManager.PERMISSION_GRANTED) }
    var requested by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        granted = isGranted
        requested = true
    }
    if (granted) content() else {
        Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.LibraryMusic, null, Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(20.dp))
            Text("Let OpenTune access your music", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("OpenTune needs music access to find songs stored on this device. Your library stays on your device unless you explicitly use an online feature.", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))
            Button(onClick = { launcher.launch(musicPermission()) }) { Text(if (requested) "Allow music access" else "Continue") }
            if (requested) { Spacer(Modifier.height(12.dp)); Text("Music access is required for your local library.", style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable
private fun OpenTuneApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf(demoTracks.first()) }
    var showNowPlaying by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember(context) { PlayerController(context) }
    DisposableEffect(player) { onDispose { player.release() } }

    fun toDemo(local: LocalTrack) = DemoTrack(
        Track(id = local.id.toString(), title = local.title, artist = local.artist, album = local.album, uri = local.uri, artworkUri = local.artworkUri),
        Color(0xFF5B4B8A)
    )
    fun playLocalTrack(local: LocalTrack) {
        current = toDemo(local)
        player.play(current.track)
        isPlaying = true
    }
    fun playLocalAlbum(tracks: List<LocalTrack>) {
        val first = tracks.firstOrNull() ?: return
        current = toDemo(first)
        player.playAll(tracks.map { toDemo(it).track })
        isPlaying = true
    }

    if (showNowPlaying) {
        NowPlayingScreen(current.track.title, current.track.artist, isPlaying, current.track.artworkUri, { showNowPlaying = false }, {
            player.playPause()
            isPlaying = !isPlaying
        })
        return
    }

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
                0 -> HomeScreen(current, isPlaying, onPlay = { selected -> current = selected; isPlaying = true; player.play(selected.track) })
                1 -> PlaceholderScreen("Search", "Find songs, artists, albums and playlists")
                2 -> LocalLibraryScreen(onTrackClick = ::playLocalTrack, onPlayAlbum = ::playLocalAlbum)
            }
            MiniPlayer(current, isPlaying, onOpen = { showNowPlaying = true }, onTogglePlay = { player.playPause(); isPlaying = !isPlaying })
        }
    }
}

@Composable
private fun HomeScreen(current: DemoTrack, isPlaying: Boolean, onPlay: (DemoTrack) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Good evening", style = MaterialTheme.typography.labelLarge); Text("Discover", fontSize = 30.sp, fontWeight = FontWeight.Bold) }
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
        Text("Currently playing: ${current.track.title}", style = MaterialTheme.typography.labelMedium)
        if (isPlaying) Text("Playing", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TrackCard(item: DemoTrack, onPlay: (DemoTrack) -> Unit) {
    Column(Modifier.width(150.dp).clickable { onPlay(item) }) {
        Box(Modifier.size(150.dp).clip(RoundedCornerShape(20.dp)).background(item.color)) {
            Text("♪", Modifier.align(Alignment.Center), color = Color.White, fontSize = 52.sp)
            Surface(Modifier.align(Alignment.BottomEnd).padding(8.dp), shape = RoundedCornerShape(50), color = Color.White) { Icon(Icons.Default.PlayArrow, "Play", Modifier.padding(7.dp).size(20.dp)) }
        }
        Spacer(Modifier.height(8.dp)); Text(item.track.title, fontWeight = FontWeight.SemiBold, maxLines = 1); Text(item.track.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}

@Composable
private fun PlaylistRow(title: String, subtitle: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) { Text("♫", fontSize = 25.sp) }
        Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, style = MaterialTheme.typography.bodySmall) }; IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, "More") }
    }
}

@Composable
private fun MiniPlayer(item: DemoTrack, isPlaying: Boolean, onOpen: () -> Unit, onTogglePlay: () -> Unit) {
    Surface(shadowElevation = 8.dp) {
        Row(Modifier.fillMaxWidth().clickable(onClick = onOpen).padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(item.color), contentAlignment = Alignment.Center) { Text("♪", color = Color.White, fontSize = 22.sp) }
            Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(item.track.title, fontWeight = FontWeight.SemiBold, maxLines = 1); Text(item.track.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1) }
            IconButton(onClick = onTogglePlay) { Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "Play or pause") }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, subtitle: String) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { Text(title, fontSize = 30.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Text(subtitle, style = MaterialTheme.typography.bodyLarge) }
}
