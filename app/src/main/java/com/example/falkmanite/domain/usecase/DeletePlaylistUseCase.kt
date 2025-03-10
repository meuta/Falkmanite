package com.example.falkmanite.domain.usecase

import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.Mode
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.Playlist
import com.example.falkmanite.domain.Repository
import com.example.falkmanite.domain.UseCase
import javax.inject.Inject

class DeletePlaylistUseCase @Inject constructor(
    private val repository: Repository,
    private val playerState: InMemoryCache<PlayerState>
) : UseCase<Playlist, PlayerState> {
    override fun invoke(data: Playlist): PlayerState {
        repository.deletePlaylist(data)
        var state = playerState.read()
        val playlists = repository.playlists()
//        if (state.currentPlaylist.id == data.id || state.mode == Mode.ADD_PLAYLIST) {
        if (state.currentPlaylist.title == data.title || state.mode == Mode.ADD_PLAYLIST) {
            val songs = repository.allAvailableSongs()
            state = state.copy(songsOfPlaylist = songs, currentPlaylist = playlists.first())
        }
        state.playlists = playlists
        state.updateSelection()
        return playerState.save(state)
    }
}
