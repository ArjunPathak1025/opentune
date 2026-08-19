package com.arjunpathak.opentune.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.arjunpathak.opentune.model.Track
import com.google.common.util.concurrent.ListenableFuture

class PlayerController(context: Context) {
    private val controllerFuture: ListenableFuture<MediaController> =
        MediaController.Builder(context, SessionToken(context, ComponentName(context, PlaybackService::class.java))).buildAsync()

    private fun mediaItem(track: Track): MediaItem = MediaItem.Builder()
        .setMediaId(track.id)
        .setUri(track.uri)
        .setMediaMetadata(androidx.media3.common.MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artist)
            .setAlbumTitle(track.album)
            .setArtworkUri(track.artworkUri?.let(android.net.Uri::parse))
            .build())
        .build()

    private fun withController(action: (MediaController) -> Unit) {
        controllerFuture.addListener({ action(controllerFuture.get()) }, { it.run() })
    }

    fun play(track: Track) = withController { controller ->
        controller.setMediaItem(mediaItem(track))
        controller.prepare()
        controller.play()
    }

    fun playAll(tracks: List<Track>) {
        if (tracks.isEmpty()) return
        withController { controller ->
            controller.setMediaItems(tracks.map(::mediaItem))
            controller.prepare()
            controller.play()
        }
    }

    fun playPause() = withController { controller -> if (controller.isPlaying) controller.pause() else controller.play() }
    fun next() = withController { it.seekToNextMediaItem() }
    fun previous() = withController { it.seekToPreviousMediaItem() }
    fun seekTo(positionMs: Long) = withController { it.seekTo(positionMs) }
    fun seekFraction(fraction: Float) = withController { controller ->
        val duration = controller.duration
        if (duration > 0) controller.seekTo((duration * fraction.coerceIn(0f, 1f)).toLong())
    }
    fun setShuffleEnabled(enabled: Boolean) = withController { it.shuffleModeEnabled = enabled }
    fun setRepeatMode(mode: Int) = withController { it.repeatMode = mode }
    fun currentPosition(onResult: (Long) -> Unit) = withController { onResult(it.currentPosition) }
    fun duration(onResult: (Long) -> Unit) = withController { onResult(it.duration.coerceAtLeast(0L)) }
    fun release() = MediaController.releaseFuture(controllerFuture)

    companion object {
        const val REPEAT_OFF = Player.REPEAT_MODE_OFF
        const val REPEAT_ONE = Player.REPEAT_MODE_ONE
        const val REPEAT_ALL = Player.REPEAT_MODE_ALL
    }
}
