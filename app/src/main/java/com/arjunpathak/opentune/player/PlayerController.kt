package com.arjunpathak.opentune.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.arjunpathak.opentune.model.Track
import com.google.common.util.concurrent.ListenableFuture

class PlayerController(context: Context) {
    private val controllerFuture: ListenableFuture<MediaController> =
        MediaController.Builder(
            context,
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        ).buildAsync()

    private fun mediaItem(track: Track): MediaItem =
        MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(track.uri)
            .setMediaMetadata(
                androidx.media3.common.MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.artist)
                    .setAlbumTitle(track.album)
                    .setArtworkUri(track.artworkUri?.let(android.net.Uri::parse))
                    .build()
            )
            .build()

    fun play(track: Track) {
        controllerFuture.addListener({
            val controller = controllerFuture.get()
            controller.setMediaItem(mediaItem(track))
            controller.prepare()
            controller.play()
        }, { it.run() })
    }

    fun playAll(tracks: List<Track>) {
        if (tracks.isEmpty()) return
        controllerFuture.addListener({
            val controller = controllerFuture.get()
            controller.setMediaItems(tracks.map(::mediaItem))
            controller.prepare()
            controller.play()
        }, { it.run() })
    }

    fun playPause() {
        controllerFuture.addListener({
            val controller = controllerFuture.get()
            if (controller.isPlaying) controller.pause() else controller.play()
        }, { it.run() })
    }

    fun release() {
        MediaController.releaseFuture(controllerFuture)
    }
}
