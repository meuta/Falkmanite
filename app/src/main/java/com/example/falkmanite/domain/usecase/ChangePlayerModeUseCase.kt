package com.example.falkmanite.domain.usecase

import com.example.falkmanite.domain.CombinedUseCase
import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.Mode
import com.example.falkmanite.domain.PlayerState
import javax.inject.Inject

class ChangePlayerModeUseCase @Inject constructor(
    private val playerState: InMemoryCache<PlayerState>
) : CombinedUseCase<Int, PlayerState> {
    override fun invoke(data: Int): PlayerState {
        val state = playerState.read()
        when (state.mode) {
            Mode.PLAY_MUSIC -> {
                state.updateSelection(listOf(data))
                state.mode = Mode.ADD_PLAYLIST
            }
            Mode.ADD_PLAYLIST -> {
                // do nothing
            }
        }
        return playerState.save(state)
    }

    override fun invoke(): PlayerState {
        val state = playerState.read()
        when (state.mode) {
            Mode.PLAY_MUSIC -> state.mode = Mode.ADD_PLAYLIST
            Mode.ADD_PLAYLIST -> state.mode = Mode.PLAY_MUSIC
        }
        return playerState.save(state)
    }
}
