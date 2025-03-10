package com.example.falkmanite.domain.usecase

import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.Mode
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.Playlist
import com.example.falkmanite.domain.Repository
import com.example.falkmanite.domain.Track
import com.example.falkmanite.domain.UseCase
import com.example.falkmanite.player.AudioPlayer
import javax.inject.Inject

class LoadPlaylistUseCase @Inject constructor(
    private val repository: Repository,
    private val playerState: InMemoryCache<PlayerState>,
    private val player: AudioPlayer
) : UseCase<Playlist, PlayerState> {
    override fun invoke(data: Playlist): PlayerState {
        val state = playerState.read()
        val newCurrentPlaylist = when (state.mode) {
            Mode.PLAY_MUSIC -> data
            Mode.ADD_PLAYLIST -> state.currentPlaylist
        }

        val songs = repository.songsOfPlaylist(data)

        val newCurrentTrack =
            if (songs.contains(state.currentTrack.song) || state.mode == Mode.ADD_PLAYLIST) {
                state.currentTrack
            } else {
                player.stopTrack()
                Track(songs.first())
            }

        if (state.currentTrack != newCurrentTrack) player.createPlayer(newCurrentTrack.id)

        with(state) {
            currentPlaylist = newCurrentPlaylist
            songsOfPlaylist = songs
            currentTrack = newCurrentTrack
            updateSelection()
        }
        return playerState.save(state)
    }
}
