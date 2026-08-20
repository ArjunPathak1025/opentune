package com.arjunpathak.opentune.youtube

import android.content.Context

/**
 * Boundary for online music providers.
 *
 * YouTube Music does not expose a public Android API that lets third-party
 * apps freely stream its catalog inside their own player. OpenTune therefore
 * uses official YouTube Music deep links for online search and playback handoff.
 */
interface YouTubeMusicProvider {
    fun openSearch(context: Context, query: String)
    fun openTrack(context: Context, videoId: String)
    fun openHome(context: Context)
}

class OfficialYouTubeMusicProvider : YouTubeMusicProvider {
    override fun openSearch(context: Context, query: String) {
        YouTubeMusicLink.open(context, YouTubeMusicLink.search(query))
    }

    override fun openTrack(context: Context, videoId: String) {
        YouTubeMusicLink.open(context, YouTubeMusicLink.track(videoId))
    }

    override fun openHome(context: Context) {
        YouTubeMusicLink.open(context, android.net.Uri.parse("https://music.youtube.com/"))
    }
}
