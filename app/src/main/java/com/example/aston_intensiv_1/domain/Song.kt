package com.example.aston_intensiv_1.domain

data class Song(
    val id: Int,
    val name: String?,
    val duration: String?,
    val artist: String?,
    val cover: ByteArray?
)