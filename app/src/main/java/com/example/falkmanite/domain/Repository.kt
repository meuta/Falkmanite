package com.example.falkmanite.domain

interface Repository {
    fun playlists(): List<Playlist>
    fun songsOfPlaylist(playlist: Playlist): List<Song>
    fun allAvailableSongs(): List<Song>
    fun deletePlaylist(playlist: Playlist)
    fun addSongsToPlaylist(playlist: Playlist, songs: List<Song>)
    fun createPlaylist(title: String, songsIds: List<Int>, default: Boolean = false): Playlist
}
