package com.arjunpathak.opentune.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arjunpathak.opentune.ui.theme.OpenTuneTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = SettingsStore(this)
        setContent {
            OpenTuneTheme {
                SettingsScreen(store = store, onBack = { finish() })
            }
        }
    }
}
