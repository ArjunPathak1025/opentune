package com.arjunpathak.opentune.youtube

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun YouTubeMusicSearchCard(
    onSearch: (String) -> Unit,
    onOpenHome: () -> Unit
) {
    var query by remember { mutableStateOf("") }

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
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
            Button(
                onClick = { if (query.isNotBlank()) onSearch(query.trim()) },
                enabled = query.isNotBlank()
            ) { Text("Search") }
        }
        Button(onClick = onOpenHome) {
            Icon(Icons.Default.OpenInNew, null)
            Text(" Open YouTube Music")
        }
    }
}
