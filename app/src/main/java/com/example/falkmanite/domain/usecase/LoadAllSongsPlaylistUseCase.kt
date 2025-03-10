package com.example.falkmanite.domain.usecase

import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.Mode
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.Repository
import com.example.falkmanite.domain.Track
import com.example.falkmanite.domain.UseCase
import com.example.falkmanite.player.AudioPlayer
import javax.inject.Inject

class LoadAllSongsPlaylistUseCase @Inject constructor(
    private val repository: Repository,
    private val playerState: InMemoryCache<PlayerState>,
    private val player: AudioPlayer
) : UseCase<String, PlayerState> {

    override fun invoke(data: String): PlayerState {
        val state = playerState.read()
        val songs = repository.allAvailableSongs()

        val newCurrentPlaylist = when (state.mode) {
            Mode.PLAY_MUSIC -> repository.createPlaylist(title = data, songs.map { it.id }, true)
            Mode.ADD_PLAYLIST -> state.currentPlaylist
        }

        val newCurrentTrack =
            if (songs.contains(state.currentTrack.song) || state.mode == Mode.ADD_PLAYLIST) {
                state.currentTrack
            } else {
                Track(songs.first()).also { player.createPlayer(it.id) }
            }

        with(state) {
            songsOfPlaylist = songs
            playlists = repository.playlists()
            currentPlaylist = newCurrentPlaylist
            currentTrack = newCurrentTrack
        }
//        Log.d(TAG, "invoke:   ${state.songsOfPlaylist.map { it.id to it.trackState }}")
        return playerState.save(state)
    }

    companion object {
        const val TAG = "LoadAllSongsUseCase"
    }
}
