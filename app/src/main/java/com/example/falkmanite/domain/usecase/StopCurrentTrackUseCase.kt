package com.example.falkmanite.domain.usecase

import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.UnitUseCase
import com.example.falkmanite.player.AudioPlayer
import javax.inject.Inject

class StopCurrentTrackUseCase @Inject constructor(
    private val playerState: InMemoryCache<PlayerState>,
    private val player: AudioPlayer
) : UnitUseCase<PlayerState> {
    override fun invoke(): PlayerState {
        val state = playerState.read()
        player.stopTrack()
        state.currentTrack.stop()
        return playerState.save(state)
    }
}
