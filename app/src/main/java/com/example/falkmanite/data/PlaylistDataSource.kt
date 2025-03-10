package com.example.falkmanite.data

import com.example.falkmanite.data.db.DBHelper
import com.example.falkmanite.domain.Playlist
import javax.inject.Singleton

@Singleton
class PlaylistDataSource(private val dbHelper: DBHelper) {

    private var allSongsPlaylist: Playlist? = null

    fun getAllPlaylists(): List<Playlist> = buildList {
        allSongsPlaylist?.let { add(it) }
        addAll(dbHelper.getAllPlaylists())
    }

    fun findByName(title: String): Playlist? {
        val songs = dbHelper.getSongsOfPlaylist(title)
        return if (songs.isNotEmpty()) Playlist(title, songs) else null
    }

    fun deleteByName(title: String) {
        findByName(title)?.let{ dbHelper.deletePlaylist(it.title) }
    }

    fun update(title: String, songsIds: List<Int>): Playlist {
        return dbHelper.addOrUpdatePlaylist(title,songsIds)
    }

    private fun createAllSongsPlaylist(title: String, songsIds: List<Int>): Playlist {
        allSongsPlaylist = Playlist(title, songsIds)
        return Playlist(title, songsIds)
    }

    fun createPlaylist(title: String, songsIds: List<Int>, default: Boolean): Playlist {
        return if (default) createAllSongsPlaylist(title, songsIds) else update(title, songsIds)
    }
}
