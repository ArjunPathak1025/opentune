package com.arjunpathak.opentune.audio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EqualizerScreen(
    controller: AudioEffectsController,
    onBack: () -> Unit
) {
    var presetMenu by remember { mutableStateOf(false) }
    var bass by remember { mutableStateOf(0.5f) }
    var virtualizer by remember { mutableStateOf(0.0f) }
    val bands = remember { mutableStateOf(EqualizerPreset.FLAT.levels.map { it.toFloat() }) }

    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = onBack) { Text("Back") }
            Button(onClick = { presetMenu = true }, modifier = Modifier.padding(start = 8.dp)) { Text("Preset") }
            DropdownMenu(expanded = presetMenu, onDismissRequest = { presetMenu = false }) {
                EqualizerPreset.entries.forEach { preset ->
                    DropdownMenuItem(text = { Text(preset.displayName) }, onClick = {
                        preset.levels.forEachIndexed { index, level -> controller.setBandLevel(index.toShort(), level.toShort()) }
                        presetMenu = false
                    })
                }
            }
        }
        Text("Equalizer")
        bands.value.forEachIndexed { index, value ->
            Text("Band ${index + 1}: ${value.toInt()} dB")
            Slider(value = value, onValueChange = { newValue ->
                bands.value = bands.value.toMutableList().also { it[index] = newValue }
                controller.setBandLevel(index.toShort(), newValue.toInt().toShort())
            }, valueRange = -12f..12f)
        }
        Text("Bass Boost")
        Slider(value = bass, onValueChange = { bass = it; controller.setBassStrength((it * 1000).toInt().toShort()) })
        Text("Virtualizer")
        Slider(value = virtualizer, onValueChange = { virtualizer = it; controller.setVirtualizerStrength((it * 1000).toInt().toShort()) })
    }
}
