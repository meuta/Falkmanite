package com.example.falkmanite.domain

interface Repository {
    suspend fun playlists(): List<Playlist>
    fun songsOfPlaylist(playlist: Playlist): List<Song>
    fun allAvailableSongs(): List<Song>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun addSongsToPlaylist(playlist: Playlist, songs: List<Song>)
    suspend fun createPlaylist(title: String, songsIds: List<Int>, default: Boolean = false): Playlist
}
