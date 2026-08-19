package com.arjunpathak.opentune.youtube

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Boundary for online music providers.
 *
 * YouTube Music does not expose a public Android API that lets third-party
 * apps freely stream its catalog inside their own player. OpenTune therefore
 * keeps the integration provider-based and uses the official YouTube Music
 * app/site for playback handoff instead of scraping or downloading content.
 */
interface YouTubeMusicProvider {
    fun openSearch(context: Context, query: String)
}

class OfficialYouTubeMusicProvider : YouTubeMusicProvider {
    override fun openSearch(context: Context, query: String) {
        val encoded = Uri.encode(query)
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://music.youtube.com/search?q=$encoded"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(appIntent)
    }
}
