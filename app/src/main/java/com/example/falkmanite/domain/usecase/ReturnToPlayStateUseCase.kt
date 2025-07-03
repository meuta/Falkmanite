package com.example.falkmanite.domain.usecase

import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.Mode
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.Repository
import com.example.falkmanite.domain.UnitUseCase
import javax.inject.Inject

class ReturnToPlayStateUseCase @Inject constructor(
    private val repository: Repository,
    private val playerState: InMemoryCache<PlayerState>
) : UnitUseCase<PlayerState> {
    override fun invoke(): PlayerState {
        val state = playerState.read()
        with(state) {
            updateSelection(emptyList())
            songsOfPlaylist = repository.songsOfPlaylist(currentPlaylist)
            mode = Mode.PLAY_MUSIC
        }
        return playerState.save(state)
    }
}
