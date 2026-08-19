package com.arjunpathak.opentune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arjunpathak.opentune.ui.theme.OpenTuneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OpenTuneTheme {
                var selectedTab by remember { mutableIntStateOf(0) }
                val tabs = listOf("Home", "Search", "Library")

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            tabs.forEachIndexed { index, title ->
                                NavigationBarItem(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    icon = { Text(title.take(1)) },
                                    label = { Text(title) }
                                )
                            }
                        }
                    }
                ) { padding ->
                    Text(
                        text = "Welcome to OpenTune",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}
