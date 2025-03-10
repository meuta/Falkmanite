package com.example.falkmanite.domain.usecase

import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.UseCase
import javax.inject.Inject

class AddSongToSelectionUseCase @Inject constructor(
    private val playerState: InMemoryCache<PlayerState>
) : UseCase<Int, PlayerState> {
    override fun invoke(data: Int): PlayerState {
        val state = playerState.read()
//        Log.d(TAG, "invoke: state = ${state.allSongs.map { it.current }}")
//        Log.d(TAG, "invoke: state = ${state.allSongs.map { it.trackState }}")
//        Log.d(TAG, "invoke: state = ${state.selectedSongs}")
        val selected = state.selectedSongs.toMutableSet()
        val isAdded = selected.add(data)
        if (!isAdded) {
            selected.remove(data)
        }
        state.updateSelection(selected)
//        Log.d(TAG, "invoke: state = ${state.selectedSongs}")
        return playerState.save(state)
    }

    companion object {
        const val TAG = "AddSongToSelection"
    }
}
