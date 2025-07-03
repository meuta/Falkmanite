package com.example.falkmanite.domain.usecase

import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.Mode
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.Repository
import com.example.falkmanite.domain.SuspendUseCase
import javax.inject.Inject

class CreatePlaylistUseCase @Inject constructor(
    private val repository: Repository,
    private val playerState: InMemoryCache<PlayerState>
) : SuspendUseCase<String, PlayerState> {

    override suspend fun invoke(data: String): PlayerState {
        val state = playerState.read()
        repository.createPlaylist(title = data, state.selectedSongs.toList())
        with(state) {
            playlists = repository.playlists()
            updateSelection(emptyList())
            mode = Mode.PLAY_MUSIC
        }
        return playerState.save(state)
    }
}
