package com.arjunpathak.opentune.library

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun AlbumArtwork(
    artworkUri: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier.size(160.dp)
) {
    if (artworkUri.isNullOrBlank()) {
        ArtworkPlaceholder(contentDescription, modifier)
    } else {
        Box(modifier = modifier.background(Color(0xFF302B3D)), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = Uri.parse(artworkUri),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onError = { /* The background remains visible if artwork cannot be decoded. */ }
            )
        }
    }
}

@Composable
private fun ArtworkPlaceholder(label: String?, modifier: Modifier) {
    val palettes = listOf(
        listOf(Color(0xFF5B4B8A), Color(0xFF2F6F8F)),
        listOf(Color(0xFF8A4F5B), Color(0xFF4A3157)),
        listOf(Color(0xFF356B5B), Color(0xFF27435D)),
        listOf(Color(0xFF8A6B3F), Color(0xFF533A62))
    )
    val palette = palettes[(label?.hashCode()?.absoluteValue ?: 0) % palettes.size]
    val initials = label
        ?.trim()
        ?.split(Regex("\\s+"))
        ?.filter { it.isNotEmpty() }
        ?.take(2)
        ?.joinToString("") { it.first().uppercase() }
        ?.take(2)
        .orEmpty()

    Box(
        modifier = modifier.background(Brush.linearGradient(palette)),
        contentAlignment = Alignment.Center
    ) {
        if (initials.isNotEmpty()) {
            Text(initials, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
        } else {
            Icon(Icons.Default.MusicNote, label, tint = Color.White, modifier = Modifier.size(48.dp))
        }
    }
}

private val Int.absoluteValue: Int
    get() = if (this == Int.MIN_VALUE) Int.MAX_VALUE else kotlin.math.abs(this)
