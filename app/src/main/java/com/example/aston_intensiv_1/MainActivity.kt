package com.example.aston_intensiv_1

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.aston_intensiv_1.databinding.ActivityMainBinding
import com.example.aston_intensiv_1.domain.MainState
import com.example.aston_intensiv_1.domain.Song
import com.example.aston_intensiv_1.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var player: MediaPlayer
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        initViewModel()
        initButtons()
        viewModel.getState()
    }

    private fun initViewModel() {
        viewModel.state.observe(this) { state ->
            initPlayer(state)
            updatePlayButton()
            initSongInfo(state.musicList[state.position])
        }
    }

    private fun initPlayer(state: MainState) {
        if (!this::player.isInitialized) {
            player = MediaPlayer.create(this, state.musicList[state.position].id)
            player.setOnCompletionListener {
                viewModel.playNext(player.isPlaying)
            }
        } else {
            player.stop()
            player.reset()
            player = MediaPlayer.create(this, state.musicList[state.position].id)
            if (state.isPlaying) {
                player.start()
            }
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
            if (player.isPlaying) {
                player.pause()
            } else {
                player.start()
            }
            updatePlayButton()
        }
        binding.btnNext.setOnClickListener {
            viewModel.playNext(player.isPlaying)
        }
        binding.btnPrevious.setOnClickListener {
            viewModel.playPrevious(player.isPlaying)
        }
    }

    private fun updatePlayButton() {
        if (player.isPlaying) {
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

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}