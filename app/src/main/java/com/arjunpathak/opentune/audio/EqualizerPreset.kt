package com.arjunpathak.opentune.audio

enum class EqualizerPreset(val displayName: String, val levels: IntArray) {
    FLAT("Flat", intArrayOf(0, 0, 0, 0, 0)),
    ROCK("Rock", intArrayOf(4, 2, -1, 2, 4)),
    POP("Pop", intArrayOf(-1, 2, 4, 2, -1)),
    CLASSICAL("Classical", intArrayOf(3, 2, 0, 2, 3)),
    BASS_BOOST("Bass Boost", intArrayOf(6, 4, 1, 0, 0))
}
