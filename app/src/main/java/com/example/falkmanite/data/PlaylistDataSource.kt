package com.example.falkmanite.data

import com.example.falkmanite.data.db.PlaylistDao
import com.example.falkmanite.domain.Playlist
import javax.inject.Singleton

@Singleton
class PlaylistDataSource(private val dao: PlaylistDao) {

    private var allSongsPlaylist: Playlist? = null

    suspend fun getAllPlaylists(): List<Playlist> = buildList {
        allSongsPlaylist?.let { add(it) }
        addAll(dao.getAllPlaylists())
    }

    suspend fun findByName(title: String): Playlist? {
        val songs = dao.getSongsOfPlaylist(title)
        return if (songs.isNotEmpty()) Playlist(title, songs) else null
    }


    suspend fun deleteByName(title: String) {
        findByName(title)?.let{ dao.deletePlaylist(it.title) }
    }

    suspend fun update(title: String, songsIds: List<Int>): Playlist {
        return dao.addOrUpdatePlaylist(title, songsIds)
    }

    private fun createAllSongsPlaylist(title: String, songsIds: List<Int>): Playlist {
        allSongsPlaylist = Playlist(title, songsIds)
        return Playlist(title, songsIds)
    }

    suspend fun createPlaylist(title: String, songsIds: List<Int>, default: Boolean): Playlist {
        return if (default) createAllSongsPlaylist(title, songsIds) else update(title, songsIds)
    }
}
