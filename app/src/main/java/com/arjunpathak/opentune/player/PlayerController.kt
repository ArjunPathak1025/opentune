package com.arjunpathak.opentune.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.arjunpathak.opentune.model.Track
import com.google.common.util.concurrent.ListenableFuture

/** UI-side controller for the app-wide Media3 playback session. */
class PlayerController(context: Context) {
    private val controllerFuture: ListenableFuture<MediaController> =
        MediaController.Builder(
            context,
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        ).buildAsync()

    data class PlaybackState(
        val isPlaying: Boolean,
        val track: Track?
    )

    private fun mediaItem(track: Track): MediaItem = MediaItem.Builder()
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

    private fun trackFrom(controller: MediaController): Track? {
        val item = controller.currentMediaItem ?: return null
        val metadata = item.mediaMetadata
        return Track(
            id = item.mediaId,
            title = metadata.title?.toString().orEmpty(),
            artist = metadata.artist?.toString().orEmpty(),
            album = metadata.albumTitle?.toString().orEmpty(),
            uri = item.localConfiguration?.uri?.toString().orEmpty(),
            artworkUri = metadata.artworkUri?.toString()
        )
    }

    private fun withController(action: (MediaController) -> Unit) {
        controllerFuture.addListener(
            { action(controllerFuture.get()) },
            { it.run() }
        )
    }

    /** Keeps Compose UI synchronized when playback changes in the background/lock screen. */
    fun observe(listener: (PlaybackState) -> Unit): () -> Unit {
        val controllerListener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                listener(PlaybackState(isPlaying, trackFrom(controllerFuture.get())))
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                listener(PlaybackState(controllerFuture.get().isPlaying, trackFrom(controllerFuture.get())))
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                listener(PlaybackState(controllerFuture.get().isPlaying, trackFrom(controllerFuture.get())))
            }
        }

        withController { controller ->
            controller.addListener(controllerListener)
            listener(PlaybackState(controller.isPlaying, trackFrom(controller)))
        }

        return {
            withController { it.removeListener(controllerListener) }
        }
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

    fun playPause() = withController { controller ->
        if (controller.isPlaying) controller.pause() else controller.play()
    }

    fun next() = withController { it.seekToNextMediaItem() }
    fun previous() = withController { it.seekToPreviousMediaItem() }
    fun seekTo(positionMs: Long) = withController { it.seekTo(positionMs) }

    fun seekFraction(fraction: Float) = withController { controller ->
        val duration = controller.duration
        if (duration > 0) {
            controller.seekTo((duration * fraction.coerceIn(0f, 1f)).toLong())
        }
    }

    fun setShuffleEnabled(enabled: Boolean) = withController { it.shuffleModeEnabled = enabled }
    fun setRepeatMode(mode: Int) = withController { it.repeatMode = mode }
    fun currentPosition(onResult: (Long) -> Unit) = withController { onResult(it.currentPosition) }
    fun duration(onResult: (Long) -> Unit) = withController { onResult(it.duration.coerceAtLeast(0L)) }

    fun queue(onResult: (items: List<Track>, currentIndex: Int) -> Unit) = withController { controller ->
        val items = (0 until controller.mediaItemCount).map { index ->
            val item = controller.getMediaItemAt(index)
            val metadata = item.mediaMetadata
            Track(
                id = item.mediaId,
                title = metadata.title?.toString().orEmpty(),
                artist = metadata.artist?.toString().orEmpty(),
                album = metadata.albumTitle?.toString().orEmpty(),
                uri = item.localConfiguration?.uri?.toString().orEmpty(),
                artworkUri = metadata.artworkUri?.toString()
            )
        }
        onResult(items, controller.currentMediaItemIndex)
    }

    fun playQueueItem(index: Int) = withController { controller ->
        if (index in 0 until controller.mediaItemCount) {
            controller.seekTo(index, 0L)
            controller.play()
        }
    }

    fun removeQueueItem(index: Int) = withController { controller ->
        if (index in 0 until controller.mediaItemCount) controller.removeMediaItem(index)
    }

    fun moveQueueItem(from: Int, to: Int) = withController { controller ->
        if (from in 0 until controller.mediaItemCount && to in 0 until controller.mediaItemCount && from != to) {
            controller.moveMediaItem(from, to)
        }
    }

    fun clearQueue() = withController { it.clearMediaItems() }

    fun release() = MediaController.releaseFuture(controllerFuture)

    companion object {
        const val REPEAT_OFF = Player.REPEAT_MODE_OFF
        const val REPEAT_ONE = Player.REPEAT_MODE_ONE
        const val REPEAT_ALL = Player.REPEAT_MODE_ALL
    }
}
