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
    fun openHome(context: Context)
}

class OfficialYouTubeMusicProvider : YouTubeMusicProvider {
    private fun openOfficial(context: Context, uri: Uri) {
        val appIntent = Intent(Intent.ACTION_VIEW, uri)
            .setPackage("com.google.android.apps.youtube.music")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(appIntent)
        } catch (_: Exception) {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun openSearch(context: Context, query: String) {
        val encoded = Uri.encode(query)
        openOfficial(context, Uri.parse("https://music.youtube.com/search?q=$encoded"))
    }

    override fun openHome(context: Context) {
        openOfficial(context, Uri.parse("https://music.youtube.com/"))
    }
}
