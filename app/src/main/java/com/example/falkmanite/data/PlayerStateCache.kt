package com.example.falkmanite.data

import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.Mode
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.Playlist
import com.example.falkmanite.domain.Song
import com.example.falkmanite.domain.Track
import javax.inject.Singleton

@Singleton
class PlayerStateCache : InMemoryCache<PlayerState> {
    private var currentTrack: Track = Track(Song(-1, "", "", 0))
    private var mode: Mode = Mode.PLAY_MUSIC
//    private var currentPlaylist: Playlist = Playlist(-1, "", emptyList())
    private var currentPlaylist: Playlist = Playlist("", emptyList())
    private var playlists: List<Playlist> = emptyList()
    private var selectedSongs: MutableSet<Int> = mutableSetOf()
    private var songsOfPlaylist: List<Song> = emptyList()

    override fun read(): PlayerState {
//        Log.d(TAG, "read: this.selectedSongs = ${this.selectedSongs}")
        return PlayerState(
            currentTrack = currentTrack,
            mode = mode,
            currentPlaylist = currentPlaylist,
            playlists = playlists,
            selectedSongs = selectedSongs,
            songsOfPlaylist = songsOfPlaylist,
        )
    }

    override fun save(data: PlayerState): PlayerState {
        save(
            data.currentTrack,
            data.mode,
            data.currentPlaylist,
            data.playlists,
            data.selectedSongs,
            data.songsOfPlaylist,
        )
//        Log.d(TAG, "save: data.selectedSongs = ${data.selectedSongs}")
        return read()
    }

    private fun save(
        currentTrack: Track = this.currentTrack,
        mode: Mode = this.mode,
        currentPlaylist: Playlist = this.currentPlaylist,
        playlists: List<Playlist> = this.playlists,
        selectedSongs: MutableSet<Int> = this.selectedSongs,
        songsOfPlaylist: List<Song> = this.songsOfPlaylist,
    ) {
        this.currentTrack = currentTrack
        this.mode = mode
        this.currentPlaylist = currentPlaylist
        this.playlists = playlists
        this.selectedSongs = selectedSongs
        this.songsOfPlaylist = songsOfPlaylist
    }

    companion object {
        const val TAG = "PlayerStateCache"
    }
}
