package com.example.falkmanite.domain

import com.example.falkmanite.ui.SongUi
import com.example.falkmanite.ui.UiState
import javax.inject.Singleton


interface PlayerStateMapper<T> {
    operator fun invoke(
        currentTrack: Track,
        mode: Mode,
        currentPlaylist: Playlist,
        playlists: List<Playlist>,
        selectedSongs: MutableSet<Int>,
        songsOfPlaylist: List<Song>,
    ): T

    @Singleton
    class ToUiState : PlayerStateMapper<UiState> {
        override fun invoke(
            currentTrack: Track,
            mode: Mode,
            currentPlaylist: Playlist,
            playlists: List<Playlist>,
            selectedSongs: MutableSet<Int>,
            songsOfPlaylist: List<Song>,
        ): UiState {
//            Log.d(TAG, "invoke: AllSongs currentTrack = ${currentTrack.let { it.id to it.trackState }}")
            val updatedSongs = if (mode == Mode.PLAY_MUSIC) {
                songsOfPlaylist.map {
                    val isCurrent = it.id == currentTrack.song.id
                    val isPlaying = isCurrent && currentTrack.isPlaying()
//                    Log.d(TAG, "invoke: AllSongs ${it.id}, $state, $isCurrent")
                    SongUi.SongUiBase(it.id, it.title, it.artist, it.duration, isCurrent, isPlaying)
                }
            } else {
                val selected = songsOfPlaylist.map { it.id }.intersect(selectedSongs)
                songsOfPlaylist.map {
                    val isSelected = it.id in selected
                    SongUi.SongUiSelector(it.id, it.title, it.artist, it.duration, isSelected)
                }
            }

//            return UiState(mode, updatedSongs, playlists)
            return UiState(mode, updatedSongs, playlists, currentTrack.duration)
        }
    }

    companion object {
        const val TAG = "PlayerStateMapper"
    }
}
