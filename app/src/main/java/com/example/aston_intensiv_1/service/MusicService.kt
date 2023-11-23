package com.example.aston_intensiv_1.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.aston_intensiv_1.MainActivity.Companion.ARTIST_NAME
import com.example.aston_intensiv_1.MainActivity.Companion.IS_PLAYING
import com.example.aston_intensiv_1.MainActivity.Companion.SONG_ID
import com.example.aston_intensiv_1.MainActivity.Companion.SONG_NAME
import com.example.aston_intensiv_1.R
import com.example.aston_intensiv_1.let2

const val MUSIC_NOTIFICATION_ID = 1
const val MUSIC_NOTIFICATION_CHANNEL = "Music"

class MusicService : Service() {

    private lateinit var player: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START_SERVICE.toString() -> onStartService()
            Actions.STOP_SERVICE.toString() -> onStopService()
            Actions.PLAY.toString() -> onPlay(intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onPlay(intent: Intent) {
        val songIdNullable = intent.extras?.getInt(SONG_ID)
        val isPlayingNullable = intent.extras?.getBoolean(IS_PLAYING)
        let2(songIdNullable, isPlayingNullable) { songId, isPlaying ->
            initPlayer(songId, isPlaying)
        }

        val songNameNullable = intent.extras?.getString(SONG_NAME)
        val artistNameNullable = intent.extras?.getString(ARTIST_NAME)
        let2(songNameNullable, artistNameNullable) { songName, artistName ->
            updateNotification(songName, artistName)
        }
    }

    private fun initPlayer(songId: Int, isPlaying: Boolean) {
        handleNewSong(songId)
        if (isPlaying) {
            player.start()
        } else {
            player.pause()
        }
    }

    private fun handleNewSong(songId: Int) {
        player.stop()
        player.reset()
        player = MediaPlayer.create(this, songId)
    }

    private fun onStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                MUSIC_NOTIFICATION_ID,
                getNotification("NAME", "ARTIST"),
                FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(
                MUSIC_NOTIFICATION_ID,
                getNotification("NAME", "ARTIST"),
            )
        }
        if (!this::player.isInitialized) {
            player = MediaPlayer.create(this, R.raw.zabej_lerochka)
        }
    }

    private fun onStopService() {
        player.release()
        stopSelf()
    }

    private fun getNotification(songName: String, artistName: String) =
        NotificationCompat.Builder(this, MUSIC_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setContentTitle(songName)
            .setContentText(artistName)
            .build()

    private fun updateNotification(songName: String, artistName: String) {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(MUSIC_NOTIFICATION_ID, getNotification(songName, artistName))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    enum class Actions {
        START_SERVICE,
        STOP_SERVICE,
        PLAY
    }
}