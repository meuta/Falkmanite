package com.example.falkmanite.domain

import androidx.fragment.app.Fragment
import com.example.falkmanite.ui.MainAddPlaylistFragment
import com.example.falkmanite.ui.MainPlayerControllerFragment
import com.example.falkmanite.ui.SongUi

interface UiStateMapper<T> {
    operator fun invoke(state: Mode, songs: List<SongUi>, playlists: List<Playlist>): T
    operator fun invoke(state: Mode): T = invoke(state, emptyList(), emptyList())

    class ToFragment : UiStateMapper<Fragment> {
        override fun invoke(
            state: Mode,
            songs: List<SongUi>,
            playlists: List<Playlist>
        ): Fragment {
            return when (state) {
                Mode.PLAY_MUSIC -> MainPlayerControllerFragment.newInstance()
                Mode.ADD_PLAYLIST -> MainAddPlaylistFragment.newInstance()
            }
        }
    }
}
