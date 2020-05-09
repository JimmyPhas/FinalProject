package com.example.finalproject

data class LyricData (
    val art: Artist,
    val mus: List<Music>
)

data class Artist (
    val name: String
)

data class Music (
    val text: String
)