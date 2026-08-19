package com.arjunpathak.opentune.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.arjunpathak.opentune.library.AlbumArtwork
import com.arjunpathak.opentune.player.PlayerController

@Composable
fun NowPlayingScreen(
    title: String,
    artist: String,
    isPlaying: Boolean,
    artworkUri: String? = null,
    onBack: () -> Unit,
    onTogglePlay: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    onShuffle: (Boolean) -> Unit,
    onRepeat: (Int) -> Unit,
    onOpenQueue: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0.35f) }
    var shuffleEnabled by remember { mutableStateOf(false) }
    var repeatMode by remember { mutableIntStateOf(PlayerController.REPEAT_OFF) }

    fun cycleRepeat() {
        repeatMode = when (repeatMode) {
            PlayerController.REPEAT_OFF -> PlayerController.REPEAT_ALL
            PlayerController.REPEAT_ALL -> PlayerController.REPEAT_ONE
            else -> PlayerController.REPEAT_OFF
        }
        onRepeat(repeatMode)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Text("Now Playing", Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, "More") }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.size(16.dp))
            AlbumArtwork(
                artworkUri = artworkUri,
                contentDescription = title,
                modifier = Modifier.fillMaxWidth().size(320.dp).clip(RoundedCornerShape(32.dp))
            )
            Spacer(Modifier.size(26.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(artist, style = MaterialTheme.typography.bodyLarge)
                }
                IconButton(onClick = {}) { Icon(Icons.Default.FavoriteBorder, "Favorite") }
            }
            Slider(
                value = progress,
                onValueChange = { progress = it },
                onValueChangeFinished = { onSeek((progress * 1000L * 60L * 4L)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Seek", style = MaterialTheme.typography.labelSmall)
                Text("Queue controls below", style = MaterialTheme.typography.labelSmall)
            }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = onOpenQueue) { Icon(Icons.Default.QueueMusic, "Queue") }
            IconButton(onClick = {
                shuffleEnabled = !shuffleEnabled
                onShuffle(shuffleEnabled)
            }) { Icon(Icons.Default.Shuffle, "Shuffle", tint = if (shuffleEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) }
            IconButton(onClick = onPrevious) { Icon(Icons.Default.FastRewind, "Previous") }
            IconButton(onClick = onTogglePlay, modifier = Modifier.size(72.dp)) {
                Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "Play or pause", modifier = Modifier.size(42.dp))
            }
            IconButton(onClick = onNext) { Icon(Icons.Default.FastForward, "Next") }
            IconButton(onClick = ::cycleRepeat) {
                Icon(Icons.Default.Repeat, if (repeatMode == PlayerController.REPEAT_ONE) "Repeat one" else "Repeat", tint = if (repeatMode != PlayerController.REPEAT_OFF) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
