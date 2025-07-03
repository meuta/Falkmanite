package com.example.falkmanite.domain

import com.example.falkmanite.ui.UiState
import javax.inject.Singleton

@Singleton
data class PlayerState(
    var currentTrack: Track,
    var mode: Mode,
    var currentPlaylist: Playlist,
    var playlists: List<Playlist>,
    var selectedSongs: MutableSet<Int>,
    var songsOfPlaylist: List<Song>,
) {

//    fun <T> map(mapper: PlayerStateMapper<T>): T {
    fun map(mapper: PlayerStateMapper<UiState>): UiState {
        return mapper(currentTrack, mode, currentPlaylist, playlists, selectedSongs, songsOfPlaylist)
    }

    fun updateSelection(selection: Iterable<Int> = selectedSongs) {
        this.selectedSongs = songsOfPlaylist.map { it.id }.intersect(selection.toSet()).toMutableSet()
    }

    fun isAnySelected() = selectedSongs.isNotEmpty()

}
