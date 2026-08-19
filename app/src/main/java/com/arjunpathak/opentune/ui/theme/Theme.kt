package com.arjunpathak.opentune.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val OpenTuneColors = darkColorScheme()

@Composable
fun OpenTuneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OpenTuneColors,
        content = content
    )
}
