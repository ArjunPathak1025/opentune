package com.arjunpathak.opentune.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arjunpathak.opentune.library.AlbumArtwork
import com.arjunpathak.opentune.player.PlayerController
import com.arjunpathak.opentune.player.QueueScreen

@Composable
fun NowPlayingScreen(
    title: String,
    artist: String,
    isPlaying: Boolean,
    artworkUri: String? = null,
    player: PlayerController,
    onBack: () -> Unit,
    onTogglePlay: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeekFraction: (Float) -> Unit,
    onShuffle: (Boolean) -> Unit,
    onRepeat: (Int) -> Unit,
    onOpenQueue: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0.35f) }
    var shuffleEnabled by remember { mutableStateOf(false) }
    var repeatMode by remember { mutableIntStateOf(PlayerController.REPEAT_OFF) }
    var showQueue by remember { mutableStateOf(false) }

    if (showQueue) {
        QueueScreen(
            player = player,
            onBack = { showQueue = false },
            onPlayingChanged = { }
        )
        return
    }

    fun cycleRepeat() {
        repeatMode = when (repeatMode) {
            PlayerController.REPEAT_OFF -> PlayerController.REPEAT_ALL
            PlayerController.REPEAT_ALL -> PlayerController.REPEAT_ONE
            else -> PlayerController.REPEAT_OFF
        }
        onRepeat(repeatMode)
    }

    val background = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
            MaterialTheme.colorScheme.background
        )
    )

    Box(Modifier.fillMaxSize().background(background)) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NOW PLAYING", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("OpenTune", style = MaterialTheme.typography.labelSmall)
                }
                IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, "More") }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AlbumArtwork(
                    artworkUri = artworkUri,
                    contentDescription = title,
                    modifier = Modifier.size(300.dp).clip(RoundedCornerShape(28.dp))
                )
                Spacer(Modifier.size(24.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(title, fontSize = 25.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.size(3.dp))
                        Text(artist, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)) {
                        IconButton(onClick = {}) { Icon(Icons.Default.FavoriteBorder, "Favorite") }
                    }
                }
                Spacer(Modifier.size(14.dp))
                Slider(value = progress, onValueChange = { progress = it }, onValueChangeFinished = { onSeekFraction(progress) }, modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Playback", style = MaterialTheme.typography.labelSmall)
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
                }
            }

            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = { shuffleEnabled = !shuffleEnabled; onShuffle(shuffleEnabled) }) {
                    Icon(Icons.Default.Shuffle, "Shuffle", tint = if (shuffleEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = onPrevious) { Icon(Icons.Default.FastRewind, "Previous", Modifier.size(30.dp)) }
                Surface(Modifier.size(72.dp), CircleShape, MaterialTheme.colorScheme.primary, shadowElevation = 6.dp) {
                    IconButton(onClick = onTogglePlay) { Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, if (isPlaying) "Pause" else "Play", tint = Color.White, modifier = Modifier.size(38.dp)) }
                }
                IconButton(onClick = onNext) { Icon(Icons.Default.FastForward, "Next", Modifier.size(30.dp)) }
                IconButton(onClick = ::cycleRepeat) {
                    Icon(Icons.Default.Repeat, "Repeat", tint = if (repeatMode != PlayerController.REPEAT_OFF) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
            }

            Surface(
                onClick = { showQueue = true; onOpenQueue() },
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            ) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QueueMusic, null)
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Up Next", fontWeight = FontWeight.SemiBold)
                        Text("Open playback queue", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
