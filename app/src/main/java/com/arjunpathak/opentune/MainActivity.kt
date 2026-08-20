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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arjunpathak.opentune.library.AlbumArtwork
import com.arjunpathak.opentune.library.FavoritesScreen
import com.arjunpathak.opentune.library.FavoritesStore
import com.arjunpathak.opentune.library.HistoryStore
import com.arjunpathak.opentune.library.LibraryViewModel
import com.arjunpathak.opentune.library.LocalLibraryScreen
import com.arjunpathak.opentune.library.LocalTrack
import com.arjunpathak.opentune.library.PlaylistStore
import com.arjunpathak.opentune.library.PlaylistsScreen
import com.arjunpathak.opentune.library.SearchScreen
import com.arjunpathak.opentune.model.Track
import com.arjunpathak.opentune.player.PlayerController
import com.arjunpathak.opentune.settings.SettingsScreen
import com.arjunpathak.opentune.settings.SettingsStore
import com.arjunpathak.opentune.ui.NowPlayingScreen
import com.arjunpathak.opentune.ui.theme.OpenTuneTheme
import com.arjunpathak.opentune.youtube.OfficialYouTubeMusicProvider
import com.arjunpathak.opentune.youtube.YouTubeMusicSearchCard

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
        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val settingsStore = remember(context) { SettingsStore(context) }
            var darkMode by remember { mutableStateOf(settingsStore.isDarkMode()) }
            OpenTuneTheme(darkTheme = darkMode) {
                PermissionGate {
                    OpenTuneApp(onDarkModeChanged = { darkMode = it })
                }
            }
        }
    }
}

private fun musicPermission(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE

@Composable
private fun PermissionGate(content: @Composable () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var granted by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, musicPermission()) == PackageManager.PERMISSION_GRANTED) }
    var requested by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> granted = isGranted; requested = true }
    if (granted) content() else Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.LibraryMusic, null, Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(20.dp)); Text("Let OpenTune access your music", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp)); Text("OpenTune needs music access to find songs stored on this device. Your library stays on your device unless you explicitly use an online feature.", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp)); Button(onClick = { launcher.launch(musicPermission()) }) { Text(if (requested) "Allow music access" else "Continue") }
        if (requested) { Spacer(Modifier.height(12.dp)); Text("Music access is required for your local library.", style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
private fun OpenTuneApp(onDarkModeChanged: (Boolean) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf(demoTracks.first()) }
    var showNowPlaying by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember(context) { PlayerController(context) }
    val youtubeMusic = remember { OfficialYouTubeMusicProvider() }
    val libraryViewModel: LibraryViewModel = viewModel()
    val tracks by libraryViewModel.tracks.collectAsState()
    val favoritesStore = remember(context) { FavoritesStore(context) }
    val playlistStore = remember(context) { PlaylistStore(context) }
    val historyStore = remember(context) { HistoryStore(context) }
    val settingsStore = remember(context) { SettingsStore(context) }
    var favoriteIds by remember { mutableStateOf(favoritesStore.getIds()) }

    LaunchedEffect(Unit) { libraryViewModel.refresh() }
    DisposableEffect(player) { onDispose { player.release() } }

    fun toDemo(local: LocalTrack) = DemoTrack(Track(id = local.id.toString(), title = local.title, artist = local.artist, album = local.album, uri = local.uri, artworkUri = local.artworkUri), Color(0xFF5B4B8A))
    fun playLocalTrack(local: LocalTrack) {
        current = toDemo(local)
        player.play(current.track)
        historyStore.record(local.id)
        isPlaying = true
    }
    fun playLocalAlbum(tracksToPlay: List<LocalTrack>) {
        val first = tracksToPlay.firstOrNull() ?: return
        current = toDemo(first)
        player.playAll(tracksToPlay.map { toDemo(it).track })
        historyStore.record(first.id)
        isPlaying = true
    }
    fun toggleFavorite(track: LocalTrack) { favoriteIds = favoritesStore.toggle(track.id) }

    val recentTracks = historyStore.getIds().mapNotNull { id -> tracks.firstOrNull { it.id == id } }
    val favoriteTracks = favoriteIds.mapNotNull { id -> tracks.firstOrNull { it.id == id } }
    val playlists = playlistStore.getPlaylists()

    if (showNowPlaying) {
        NowPlayingScreen(
            title = current.track.title,
            artist = current.track.artist,
            isPlaying = isPlaying,
            artworkUri = current.track.artworkUri,
            onBack = { showNowPlaying = false },
            onTogglePlay = { player.playPause(); isPlaying = !isPlaying },
            onPrevious = { player.previous() },
            onNext = { player.next() },
            onSeekFraction = { player.seekFraction(it) },
            onShuffle = { player.setShuffleEnabled(it) },
            onRepeat = { player.setRepeatMode(it) },
            onOpenQueue = {}
        )
        return
    }

    if (showSettings) {
        SettingsScreen(
            store = settingsStore,
            onBack = { showSettings = false },
            onDarkModeChanged = onDarkModeChanged
        )
        return
    }

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        NavigationBar {
            NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
            NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
            NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = { Icon(Icons.Default.LibraryMusic, null) }, label = { Text("Library") })
            NavigationBarItem(selected = selectedTab == 3, onClick = { selectedTab = 3 }, icon = { Icon(if (favoriteIds.isEmpty()) Icons.Default.FavoriteBorder else Icons.Default.Favorite, null) }, label = { Text("Favorites") })
            NavigationBarItem(selected = selectedTab == 4, onClick = { selectedTab = 4 }, icon = { Icon(Icons.Default.LibraryMusic, null) }, label = { Text("Playlists") })
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                0 -> HomeScreen(
                    current = current,
                    isPlaying = isPlaying,
                    recentTracks = recentTracks,
                    favoriteTracks = favoriteTracks,
                    playlists = playlists,
                    libraryTracks = tracks,
                    onOpenFavorites = { selectedTab = 3 },
                    onOpenPlaylists = { selectedTab = 4 },
                    onOpenSettings = { showSettings = true },
                    onPlay = { selected -> current = selected; isPlaying = true; player.play(selected.track) }
                )
                1 -> Column(Modifier.fillMaxSize()) {
                    YouTubeMusicSearchCard(onSearch = { youtubeMusic.openSearch(context, it) }, onOpenHome = { youtubeMusic.openHome(context) })
                    SearchScreen(tracks, favoriteIds.toSet(), ::playLocalTrack, ::toggleFavorite)
                }
                2 -> LocalLibraryScreen(onTrackClick = ::playLocalTrack, onPlayAlbum = ::playLocalAlbum, viewModel = libraryViewModel)
                3 -> FavoritesScreen(tracks, favoriteIds, ::playLocalTrack, ::toggleFavorite)
                4 -> PlaylistsScreen(tracks, playlistStore, ::playLocalTrack, ::playLocalAlbum)
            }
            MiniPlayer(current, isPlaying, onOpen = { showNowPlaying = true }, onTogglePlay = { player.playPause(); isPlaying = !isPlaying })
        }
    }
}

@Composable
private fun HomeScreen(
    current: DemoTrack,
    isPlaying: Boolean,
    recentTracks: List<LocalTrack>,
    favoriteTracks: List<LocalTrack>,
    playlists: Map<String, List<Long>>,
    libraryTracks: List<LocalTrack>,
    onOpenFavorites: () -> Unit,
    onOpenPlaylists: () -> Unit,
    onOpenSettings: () -> Unit,
    onPlay: (DemoTrack) -> Unit
) {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val greeting = when (hour) { in 5..11 -> "Good morning"; in 12..17 -> "Good afternoon"; else -> "Good evening" }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(greeting, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Your music", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Settings, "Settings") }
            IconButton(onClick = onOpenFavorites) { Icon(if (favoriteTracks.isEmpty()) Icons.Default.FavoriteBorder else Icons.Default.Favorite, "Favorites") }
            IconButton(onClick = onOpenPlaylists) { Icon(Icons.Default.LibraryMusic, "Playlists") }
        }

        Card(Modifier.fillMaxWidth().height(190.dp), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
            Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF6C4AB6), Color(0xFF1F6E8C)))).padding(24.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("OPENTUNE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(if (favoriteTracks.isNotEmpty()) "Back to your favorites" else "Start your music journey", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Text(if (favoriteTracks.isNotEmpty()) "Pick up a song you already love" else "Build your library, discover music and make it yours", color = Color.White.copy(alpha = .82f), maxLines = 2)
                    Surface(shape = RoundedCornerShape(50), color = Color.White) {
                        Text("${if (favoriteTracks.isNotEmpty()) "Play favorites" else "Explore library"}", Modifier.padding(horizontal = 18.dp, vertical = 10.dp), fontWeight = FontWeight.SemiBold, color = Color(0xFF24202E))
                    }
                }
            }
        }

        if (recentTracks.isNotEmpty()) {
            HomeSectionTitle("Recently played")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) { items(recentTracks.take(10)) { track -> TrackCard(toDemoTrack(track), onPlay) } }
        }

        if (favoriteTracks.isNotEmpty()) {
            HomeSectionTitle("Your favorites", onMore = onOpenFavorites)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) { items(favoriteTracks.take(10)) { track -> TrackCard(toDemoTrack(track), onPlay) } }
        }

        HomeSectionTitle("Quick picks")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) { items(demoTracks) { TrackCard(it, onPlay) } }

        if (playlists.isNotEmpty()) {
            HomeSectionTitle("Your playlists", onMore = onOpenPlaylists)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(playlists.entries.toList()) { (name, ids) ->
                    val first = ids.asSequence().mapNotNull { id -> libraryTracks.firstOrNull { it.id == id } }.firstOrNull()
                    PlaylistCard(name = name, track = first, onPlay = if (first != null) ({ onPlay(toDemoTrack(first)) }) else null)
                }
            }
        }

        HomeSectionTitle("Made for you")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(listOf("Daily Discovery", "Late Night", "New Releases")) { title ->
                CuratedCard(title, when (title) { "Daily Discovery" -> "A fresh mix for today"; "Late Night" -> "Chill sounds after dark"; else -> "Explore something new" })
            }
        }

        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .55f)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text(current.track.title, fontWeight = FontWeight.SemiBold, maxLines = 1); Text(if (isPlaying) "Playing now" else "Ready to play", style = MaterialTheme.typography.bodySmall) }
                Icon(Icons.Default.PlayArrow, null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun HomeSectionTitle(title: String, onMore: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (onMore != null) Text("See all", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onMore() }.padding(4.dp))
    }
}

private fun toDemoTrack(local: LocalTrack) = DemoTrack(Track(id = local.id.toString(), title = local.title, artist = local.artist, album = local.album, uri = local.uri, artworkUri = local.artworkUri), Color(0xFF5B4B8A))

@Composable
private fun TrackCard(item: DemoTrack, onPlay: (DemoTrack) -> Unit) {
    Column(Modifier.width(150.dp).clickable { onPlay(item) }) {
        Box(Modifier.size(150.dp)) {
            AlbumArtwork(artworkUri = item.track.artworkUri, contentDescription = item.track.album ?: item.track.title, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)))
            Surface(Modifier.align(Alignment.BottomEnd).padding(8.dp), shape = RoundedCornerShape(50), color = Color.White) { Icon(Icons.Default.PlayArrow, "Play", Modifier.padding(7.dp).size(20.dp), tint = Color(0xFF24202E)) }
        }
        Spacer(Modifier.height(8.dp)); Text(item.track.title, fontWeight = FontWeight.SemiBold, maxLines = 1); Text(item.track.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}

@Composable
private fun PlaylistCard(name: String, track: LocalTrack?, onPlay: (() -> Unit)?) {
    Column(Modifier.width(180.dp)) {
        Box(Modifier.size(180.dp).clip(RoundedCornerShape(20.dp)).clickable(enabled = onPlay != null) { onPlay?.invoke() }) {
            if (track != null) AlbumArtwork(track.artworkUri, name, Modifier.fillMaxSize()) else Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) { Icon(Icons.Default.LibraryMusic, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            Surface(Modifier.align(Alignment.BottomEnd).padding(8.dp), shape = RoundedCornerShape(50), color = Color.White) { Icon(Icons.Default.PlayArrow, "Play playlist", Modifier.padding(7.dp).size(20.dp), tint = Color(0xFF24202E)) }
        }
        Spacer(Modifier.height(8.dp)); Text(name, fontWeight = FontWeight.SemiBold, maxLines = 1); Text("Playlist", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun CuratedCard(title: String, subtitle: String) {
    Column(Modifier.width(180.dp)) {
        Box(Modifier.size(180.dp).clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.tertiaryContainer))), contentAlignment = Alignment.Center) { Text("♫", fontSize = 52.sp, color = MaterialTheme.colorScheme.onPrimaryContainer) }
        Spacer(Modifier.height(8.dp)); Text(title, fontWeight = FontWeight.SemiBold, maxLines = 1); Text(subtitle, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}

@Composable
private fun MiniPlayer(item: DemoTrack, isPlaying: Boolean, onOpen: () -> Unit, onTogglePlay: () -> Unit) {
    Surface(shadowElevation = 8.dp) {
        Row(Modifier.fillMaxWidth().clickable(onClick = onOpen).padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            AlbumArtwork(artworkUri = item.track.artworkUri, contentDescription = item.track.album ?: item.track.title, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)))
            Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(item.track.title, fontWeight = FontWeight.SemiBold, maxLines = 1); Text(item.track.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1) }; IconButton(onClick = onTogglePlay) { Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "Play or pause") }
        }
    }
}
