package com.arjunpathak.opentune.youtube

import android.content.Context
import android.content.Intent
import android.net.Uri

/** Official YouTube Music deep-link helpers. OpenTune hands online playback to YouTube Music. */
object YouTubeMusicLink {
    private const val PACKAGE = "com.google.android.apps.youtube.music"
    private const val BASE = "https://music.youtube.com"

    fun search(query: String): Uri =
        Uri.parse(BASE).buildUpon()
            .appendPath("search")
            .appendQueryParameter("q", query)
            .build()

    fun track(videoId: String): Uri =
        Uri.parse(BASE).buildUpon()
            .appendPath("watch")
            .appendQueryParameter("v", videoId)
            .build()

    fun open(context: Context, uri: Uri) {
        val appIntent = Intent(Intent.ACTION_VIEW, uri)
            .setPackage(PACKAGE)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(appIntent)
        } catch (_: Exception) {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}
