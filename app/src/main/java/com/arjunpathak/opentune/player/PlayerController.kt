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
    private val controllerFuture: ListenableFuture<MediaController> = MediaController.Builder(context, SessionToken(context, ComponentName(context, PlaybackService::class.java))).buildAsync()

    data class PlaybackState(val isPlaying: Boolean, val track: Track?, val positionMs: Long, val durationMs: Long)

    private fun mediaItem(track: Track): MediaItem = MediaItem.Builder().setMediaId(track.id).setUri(track.uri).setMediaMetadata(androidx.media3.common.MediaMetadata.Builder().setTitle(track.title).setArtist(track.artist).setAlbumTitle(track.album).setArtworkUri(track.artworkUri?.let(android.net.Uri::parse)).build()).build()

    private fun trackFrom(controller: MediaController): Track? {
        val item = controller.currentMediaItem ?: return null
        val metadata = item.mediaMetadata
        return Track(item.mediaId, metadata.title?.toString().orEmpty(), metadata.artist?.toString().orEmpty(), metadata.albumTitle?.toString().orEmpty(), item.localConfiguration?.uri?.toString().orEmpty(), metadata.artworkUri?.toString())
    }

    private fun state(controller: MediaController) = PlaybackState(controller.isPlaying, trackFrom(controller), controller.currentPosition.coerceAtLeast(0L), controller.duration.coerceAtLeast(0L))
    private fun withController(action: (MediaController) -> Unit) { controllerFuture.addListener({ action(controllerFuture.get()) }, { it.run() }) }

    fun observe(listener: (PlaybackState) -> Unit): () -> Unit {
        val controllerListener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) = listener(state(controllerFuture.get()))
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) = listener(state(controllerFuture.get()))
            override fun onPlaybackStateChanged(playbackState: Int) = listener(state(controllerFuture.get()))
            override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) = listener(state(controllerFuture.get()))
        }
        withController { controller -> controller.addListener(controllerListener); listener(state(controller)) }
        return { withController { it.removeListener(controllerListener) } }
    }

    fun play(track: Track) = withController { controller -> controller.setMediaItem(mediaItem(track)); controller.prepare(); controller.play() }
    fun playAll(tracks: List<Track>) { if (tracks.isEmpty()) return; withController { controller -> controller.setMediaItems(tracks.map(::mediaItem)); controller.prepare(); controller.play() } }
    fun playPause() = withController { if (it.isPlaying) it.pause() else it.play() }
    fun next() = withController { it.seekToNextMediaItem() }
    fun previous() = withController { it.seekToPreviousMediaItem() }
    fun seekTo(positionMs: Long) = withController { it.seekTo(positionMs) }
    fun seekFraction(fraction: Float) = withController { c -> if (c.duration > 0) c.seekTo((c.duration * fraction.coerceIn(0f, 1f)).toLong()) }
    fun setShuffleEnabled(enabled: Boolean) = withController { it.shuffleModeEnabled = enabled }
    fun setRepeatMode(mode: Int) = withController { it.repeatMode = mode }
    fun currentPosition(onResult: (Long) -> Unit) = withController { onResult(it.currentPosition) }
    fun duration(onResult: (Long) -> Unit) = withController { onResult(it.duration.coerceAtLeast(0L)) }
    fun queue(onResult: (items: List<Track>, currentIndex: Int) -> Unit) = withController { c -> onResult((0 until c.mediaItemCount).map { i -> val item = c.getMediaItemAt(i); val m = item.mediaMetadata; Track(item.mediaId, m.title?.toString().orEmpty(), m.artist?.toString().orEmpty(), m.albumTitle?.toString().orEmpty(), item.localConfiguration?.uri?.toString().orEmpty(), m.artworkUri?.toString()) }, c.currentMediaItemIndex) }
    fun playQueueItem(index: Int) = withController { c -> if (index in 0 until c.mediaItemCount) { c.seekTo(index, 0L); c.play() } }
    fun removeQueueItem(index: Int) = withController { c -> if (index in 0 until c.mediaItemCount) c.removeMediaItem(index) }
    fun moveQueueItem(from: Int, to: Int) = withController { c -> if (from in 0 until c.mediaItemCount && to in 0 until c.mediaItemCount && from != to) c.moveMediaItem(from, to) }
    fun clearQueue() = withController { it.clearMediaItems() }
    fun release() = MediaController.releaseFuture(controllerFuture)

    companion object { const val REPEAT_OFF = Player.REPEAT_MODE_OFF; const val REPEAT_ONE = Player.REPEAT_MODE_ONE; const val REPEAT_ALL = Player.REPEAT_MODE_ALL }
}
