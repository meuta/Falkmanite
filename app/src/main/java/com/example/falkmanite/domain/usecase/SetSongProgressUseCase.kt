package com.example.falkmanite.domain.usecase

import com.example.falkmanite.domain.UseCase
import com.example.falkmanite.player.AudioPlayer
import javax.inject.Inject

class SetSongProgressUseCase @Inject constructor(
    private val player: AudioPlayer
) : UseCase<Int, Unit> {
    override fun invoke(data: Int) {
        player.seekTo(data)
    }

    companion object {
        const val TAG = "SetSongProgressUseCase"
    }
}