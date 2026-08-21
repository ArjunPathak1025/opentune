package com.arjunpathak.opentune.audio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AudioEffectsScreen(
    controller: AudioEffectsController,
    onBack: () -> Unit
) {
    var enabled by remember { mutableStateOf(true) }
    var bass by remember { mutableFloatStateOf(0f) }
    var virtualizer by remember { mutableFloatStateOf(0f) }
    var preset by remember { mutableStateOf(EqualizerPreset.FLAT) }
    var menuExpanded by remember { mutableStateOf(false) }
    val bandLabels = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz")
    var levels by remember { mutableStateOf(List(5) { 0f }) }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Audio effects", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
        Text("Changes apply to the current OpenTune playback session.", style = MaterialTheme.typography.bodySmall)

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Preset", modifier = Modifier.weight(1f))
            Button(onClick = { menuExpanded = true }) { Text(preset.displayName) }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                EqualizerPreset.entries.forEach { option ->
                    DropdownMenuItem(text = { Text(option.displayName) }, onClick = {
                        preset = option
                        levels = option.levels.map { it.toFloat() }
                        if (enabled) controller.applyPreset(option)
                        menuExpanded = false
                    })
                }
            }
        }

        Text("Equalizer", style = MaterialTheme.typography.titleMedium)
        bandLabels.forEachIndexed { index, label ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(label, modifier = Modifier.weight(.24f), style = MaterialTheme.typography.bodySmall)
                Slider(value = levels[index], onValueChange = { value ->
                    levels = levels.toMutableList().also { it[index] = value }
                    if (enabled) controller.setBandLevel(index.toShort(), (value * 100).toInt().toShort())
                }, valueRange = -10f..10f, modifier = Modifier.weight(.76f))
            }
        }

        Spacer(Modifier.height(4.dp))
        Text("Bass Boost", style = MaterialTheme.typography.titleMedium)
        Slider(value = bass, onValueChange = { bass = it; if (enabled) controller.setBassStrength((it * 1000).toInt().toShort()) }, valueRange = 0f..1f)

        Text("Virtualizer", style = MaterialTheme.typography.titleMedium)
        Slider(value = virtualizer, onValueChange = { virtualizer = it; if (enabled) controller.setVirtualizerStrength((it * 1000).toInt().toShort()) }, valueRange = 0f..1f)

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Done") }
    }
}
