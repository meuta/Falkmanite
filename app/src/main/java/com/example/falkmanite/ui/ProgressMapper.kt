package com.example.falkmanite.ui

import com.example.falkmanite.domain.ProgressState
import javax.inject.Singleton

@Singleton
class ProgressMapper(private val stringFormatter: StringFormatter) {

    operator fun invoke(progressState: ProgressState): ProgressStateUi {
        val currentTimeSting = stringFormatter.format(progressState.current)
        val currentSec = (progressState.current / 1000)
        return ProgressStateUi(currentSec, currentTimeSting, progressState.isFinished)
    }

    companion object {
        const val TAG = "PlaybackMapper"
    }
}