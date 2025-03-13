package com.example.falkmanite.domain.usecase

import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.Track
import com.example.falkmanite.domain.UseCase
import com.example.falkmanite.player.AudioPlayer
import javax.inject.Inject

class PlayOrPauseTrackUseCase @Inject constructor(
    private val playerState: InMemoryCache<PlayerState>,
    private val player: AudioPlayer
) : UseCase<Int, PlayerState> {
    override fun invoke(data: Int): PlayerState {
        val state = playerState.read()


        with(state) {
            if (currentTrack.id != Track.UNDEFINED_ID) {
                if (currentTrack.id != data) {
                    player.createPlayer(data)
                    player.playTrack()
                    currentTrack = Track(songsOfPlaylist.first { it.id == data }).apply { play() }
                } else {
                    if (player.playerIsNull()) player.createPlayer(data)
                    if (player.isPlaying()) player.pauseTrack() else player.playTrack()
                    currentTrack.switchPlaying()
                }
            }
        }
//        Log.d(TAG, "PlayOrPause AllSongs currentTrack = ${ state.currentTrack }")
//        Log.d(TAG, "PlayOrPause AllSongs = ${ state.songsOfPlaylist.map { it.id to it.trackState }}")

        return playerState.save(state)
    }

    companion object {
        const val TAG = "PlayOrPauseTrackUseCase"
    }
}
