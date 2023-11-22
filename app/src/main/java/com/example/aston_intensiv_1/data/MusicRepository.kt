package com.example.aston_intensiv_1.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.aston_intensiv_1.R
import com.example.aston_intensiv_1.domain.Song


class MusicRepository(private val context: Context) {
    private val mediaDataRetriever = MediaMetadataRetriever()
    fun getMusic() = listOf<Song>(
        parseSong(R.raw.bilbordi_ostri_lezviya),
        parseSong(R.raw.zabej_lerochka),
        parseSong(R.raw.tourism_ishi_cheloveka)
    )

    private fun parseSong(id: Int): Song {
        val mediaPath =
            Uri.parse("android.resource://" + context.packageName + "/" + id)
        mediaDataRetriever.setDataSource(context, mediaPath)

        return Song(
            id = id,
            name = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
            artist = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
            cover = mediaDataRetriever.embeddedPicture,
            duration = mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION),
        )
    }
}