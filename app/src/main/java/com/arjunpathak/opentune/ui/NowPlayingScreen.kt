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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.arjunpathak.opentune.library.AlbumArtwork

@Composable
fun NowPlayingScreen(
    title: String,
    artist: String,
    isPlaying: Boolean,
    artworkUri: String? = null,
    onBack: () -> Unit,
    onTogglePlay: () -> Unit
) {
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
            Slider(value = 0.35f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("1:12", style = MaterialTheme.typography.labelSmall)
                Text("3:48", style = MaterialTheme.typography.labelSmall)
            }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = {}) { Icon(Icons.Default.QueueMusic, "Queue") }
            IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, "Previous") }
            IconButton(onClick = onTogglePlay, modifier = Modifier.size(72.dp)) {
                Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "Play or pause", modifier = Modifier.size(42.dp))
            }
            IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, "Next") }
            IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, "More") }
        }
    }
}
