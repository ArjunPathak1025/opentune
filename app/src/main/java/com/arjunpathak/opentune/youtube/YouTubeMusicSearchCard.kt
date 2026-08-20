package com.arjunpathak.opentune.youtube

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun YouTubeMusicSearchCard(
    onSearch: (String) -> Unit,
    onOpenHome: () -> Unit
) {
    val context = LocalContext.current
    val history = remember(context) { YouTubeMusicSearchHistory(context) }
    var query by remember { mutableStateOf("") }
    var recent by remember { mutableStateOf(history.get()) }

    fun submit(value: String) {
        val clean = value.trim()
        if (clean.isBlank()) return
        recent = history.add(clean)
        query = clean
        onSearch(clean)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("YouTube Music", style = MaterialTheme.typography.titleMedium)
        Text(
            "Search the official YouTube Music catalog. Playback opens in YouTube Music.",
            style = MaterialTheme.typography.bodySmall
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("Search YouTube Music") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, "Clear search", Modifier.size(20.dp))
                        }
                    }
                }
            )
            Button(onClick = { submit(query) }, enabled = query.isNotBlank()) { Text("Search") }
        }

        if (recent.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recent.forEach { item ->
                    AssistChip(label = { Text(item, maxLines = 1) }, onClick = { submit(item) })
                }
                AssistChip(
                    label = { Text("Clear") },
                    leadingIcon = { Icon(Icons.Default.Clear, null, Modifier.size(16.dp)) },
                    onClick = { history.clear(); recent = emptyList() }
                )
            }
        }

        Button(onClick = onOpenHome) {
            Icon(Icons.Default.OpenInNew, null)
            Text(" Open YouTube Music")
        }
    }
}
