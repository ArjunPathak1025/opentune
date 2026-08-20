package com.arjunpathak.opentune.audio

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer

/** Android audio effects bound to the active Media3 audio session. */
class AudioEffectsController(private var audioSessionId: Int) {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    fun attach(sessionId: Int) {
        release()
        audioSessionId = sessionId
        if (sessionId == 0) return
        runCatching {
            equalizer = Equalizer(0, sessionId).apply { enabled = true }
            bassBoost = BassBoost(0, sessionId).apply { enabled = true }
            virtualizer = Virtualizer(0, sessionId).apply { enabled = true }
        }
    }

    fun setBandLevel(band: Short, level: Short) {
        runCatching { equalizer?.setBandLevel(band, level) }
    }

    fun setBassStrength(strength: Short) {
        runCatching { bassBoost?.setStrength(strength) }
    }

    fun setVirtualizerStrength(strength: Short) {
        runCatching { virtualizer?.setStrength(strength) }
    }

    fun release() {
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
        equalizer = null
        bassBoost = null
        virtualizer = null
    }
}
