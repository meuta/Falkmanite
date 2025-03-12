package com.example.falkmanite.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.falkmanite.domain.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query("SELECT DISTINCT title FROM playlist")
    suspend fun getAllPlaylistTitles(): List<String>

    @Query("SELECT DISTINCT title FROM playlist")
    fun getAllPlaylistTitlesFlow(): Flow<List<String>>

    @Query("SELECT songsId FROM playlist WHERE title=:playlistName")
    suspend fun getSongsOfPlaylist(playlistName: String): List<Int>

    @Query("DELETE FROM playlist WHERE title=:playlistName AND songsId=:songId")
    suspend fun removeSongFromPlaylist(playlistName: String, songId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongToPlaylist(dbEntity: DBEntity)

    @Query("DELETE FROM playlist WHERE title=:title")
    suspend fun deletePlaylist(title: String)


    suspend fun getAllPlaylists(): List<Playlist> {
        return getAllPlaylistTitles().map { Playlist(it, getSongsOfPlaylist(it)) }
    }


    private suspend fun removeSongsFromPlaylist(playlistName: String, songs: Collection<Int>) {
        songs.forEach { removeSongFromPlaylist(playlistName, it) }
    }


    private suspend fun addSongsToPlaylist(playlistName: String, songs: Collection<Int>) {
        songs.forEach { addSongToPlaylist(DBEntity(DBEntity.UNDEFINED_ID, playlistName, it)) }
    }


    suspend fun addOrUpdatePlaylist(playlistName: String, songs: List<Int>): Playlist {

        val songsBefore = getSongsOfPlaylist(playlistName)
        val songsToRemove = songsBefore.subtract(songs.toSet())
        val songsToAdd = songs.subtract(songsBefore.toSet())

        removeSongsFromPlaylist(playlistName, songsToRemove)
        addSongsToPlaylist(playlistName, songsToAdd)

        return Playlist(playlistName, getSongsOfPlaylist(playlistName))
    }
}