package com.example.aston_intensiv_1.domain

data class MainState(
    val musicList: List<Song>,
    val progress: Int,
    val position: Int,
    val isPlaying: Boolean,
    val isShuffle: Boolean,
)