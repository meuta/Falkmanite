package com.example.falkmanite.domain


data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: Int,
)
//) {
//    fun <T> map(mapper: SongMapper<T>): T {
//        return mapper(id, title, artist, duration, false, false, isPlaying())
//    }
