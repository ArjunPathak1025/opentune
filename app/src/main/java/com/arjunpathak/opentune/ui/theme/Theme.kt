package com.arjunpathak.opentune.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val OpenTuneDarkColors = darkColorScheme()
private val OpenTuneLightColors = lightColorScheme()

@Composable
fun OpenTuneTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) OpenTuneDarkColors else OpenTuneLightColors,
        content = content
    )
}
