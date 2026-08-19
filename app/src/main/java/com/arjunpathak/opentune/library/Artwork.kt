package com.arjunpathak.opentune.library

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun AlbumArtwork(
    artworkUri: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier.size(160.dp)
) {
    if (artworkUri.isNullOrBlank()) {
        Box(modifier.background(Color(0xFF5B4B8A)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.MusicNote, contentDescription, tint = Color.White, modifier = Modifier.size(48.dp))
        }
    } else {
        AsyncImage(
            model = Uri.parse(artworkUri),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}
