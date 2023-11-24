package com.example.aston_intensiv_1.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
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
    private var currentSong: Int = -1

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START_SERVICE.toString() -> onStartService()
            Actions.STOP_SERVICE.toString() -> onStopService()
            Actions.LOAD.toString() -> onLoad(intent)
            Actions.PLAY.toString() -> onPlay(intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onLoad(intent: Intent) {
        val songId = intent.extras?.getInt(SONG_ID)
        if (currentSong != songId && songId != null) {
            currentSong = songId
            loadNewSong(songId)
        }

        val songNameNullable = intent.extras?.getString(SONG_NAME)
        val artistNameNullable = intent.extras?.getString(ARTIST_NAME)
        let2(songNameNullable, artistNameNullable) { songName, artistName ->
            updateNotification(songName, artistName)
        }
    }

    private fun onPlay(intent: Intent) {
        val isPlaying = intent.extras?.getBoolean(IS_PLAYING)
        isPlaying?.let {
            if (it) {
                player.start()
            } else {
                player.pause()
            }
        }
    }

    private fun loadNewSong(songId: Int) {
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
        LOAD,
        PLAY
    }
}