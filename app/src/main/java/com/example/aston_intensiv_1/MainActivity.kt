package com.example.aston_intensiv_1

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.aston_intensiv_1.databinding.ActivityMainBinding
import com.example.aston_intensiv_1.domain.MainState
import com.example.aston_intensiv_1.domain.Song
import com.example.aston_intensiv_1.presentation.MainViewModel
import com.example.aston_intensiv_1.service.MusicService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var currentSong: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        checkPermission()
        initViewModel()
    }

    override fun onStart() {
        super.onStart()
        initButtons()
    }

    private fun initViewModel() {
        viewModel.state.observe(this) { state ->
            if (currentSong != state.musicList[state.position].id) {
                currentSong = state.musicList[state.position].id
                launchService(MusicService.Actions.LOAD.toString(), state)
                if (state.isPlaying) {
                    launchService(MusicService.Actions.PLAY.toString(), state)
                }
            } else {
                launchService(MusicService.Actions.PLAY.toString(), state)
            }

            initSongInfo(state.musicList[state.position])
            updatePlayButton(state.isPlaying)
        }
        if (viewModel.state.value == null) {
            viewModel.getState()
            launchService(
                action = MusicService.Actions.START_SERVICE.toString(),
            )
        }
    }

    private fun initSongInfo(song: Song) {
        binding.songTitle.text = song.artist + " - " + song.name
        song.cover?.let {
            binding.image.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    song.cover,
                    0,
                    song.cover.size
                )
            )
        }
    }

    private fun initButtons() {
        binding.btnPlay.setOnClickListener {
            viewModel.isPlayingChanged()
        }
        binding.btnNext.setOnClickListener {
            viewModel.getNextSong()
        }
        binding.btnPrevious.setOnClickListener {
            viewModel.getPreviousSong()
        }
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        if (isPlaying) {
            binding.btnPlay.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.baseline_pause_circle_24,
                null
            )
        } else {
            binding.btnPlay.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.baseline_play_circle_24,
                null
            )
        }
    }

    private fun launchService(action: String, state: MainState? = null) {
        Intent(applicationContext, MusicService::class.java).also {
            it.action = action
            if (state != null) {
                it.putExtra(SONG_NAME, state.musicList[state.position].name)
                it.putExtra(ARTIST_NAME, state.musicList[state.position].artist)
                it.putExtra(SONG_ID, state.musicList[state.position].id)
                it.putExtra(IS_PLAYING, state.isPlaying)
            }
            startService(it)
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    companion object {
        const val SONG_NAME = "song name"
        const val ARTIST_NAME = "artist name"
        const val SONG_ID = "song id"
        const val IS_PLAYING = "is playing"
    }
}