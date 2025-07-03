package com.example.falkmanite.data

import com.example.falkmanite.domain.Playlist
import com.example.falkmanite.domain.Repository
import com.example.falkmanite.domain.Song
import javax.inject.Singleton

@Singleton
class SongRepository(
    private val songDataSource: SongDataSource,
    private val playlistDataSource: PlaylistDataSource
) : Repository {

    override suspend fun playlists() = playlistDataSource.getAllPlaylists()

    override fun songsOfPlaylist(playlist: Playlist) = songDataSource.findByIds(playlist.songsIds)

    override fun allAvailableSongs() = songDataSource.readAll()

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDataSource.deleteByName(playlist.title)
    }

    override suspend fun addSongsToPlaylist(playlist: Playlist, songs: List<Song>) {
        playlistDataSource.findByName(playlist.title)?.let {
            val songsIds = (it.songsIds + songs.map { s -> s.id }).distinct()
            playlistDataSource.update(it.title, songsIds)
        }
    }

    override suspend fun createPlaylist(title: String, songsIds: List<Int>, default: Boolean): Playlist {
        return playlistDataSource.createPlaylist(title, songsIds, default)
    }
}
