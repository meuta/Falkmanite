package com.example.falkmanite.ui

import com.example.falkmanite.domain.Mode
import com.example.falkmanite.domain.Playlist
import com.example.falkmanite.domain.UiStateMapper

data class UiState(
    val state: Mode,
    val tracks: List<SongUi>,
    val playlists: List<Playlist>
) {
    fun <T> map(mapper: UiStateMapper<T>): T {
        return mapper(state, tracks, playlists)
    }

    companion object {
        fun <T> mapDefault(mapper: UiStateMapper<T>): T {
            return mapper(Mode.PLAY_MUSIC)
        }
    }
}
