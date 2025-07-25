package com.example.falkmanite.ui

import com.example.falkmanite.domain.ProgressState
import javax.inject.Singleton

@Singleton
class ProgressMapper(private val stringFormatter: StringFormatter) {

    operator fun invoke(progressState: ProgressState): ProgressStateUi {
        val currentPositionSec = progressState.current / 1000
        val currentPositionString = stringFormatter.formatMillisToString(progressState.current)
        val durationSec = progressState.duration / 1000
        val durationString = stringFormatter.formatMillisToString(progressState.duration)

        return ProgressStateUi(
            currentPositionSec,
            currentPositionString,
            progressState.isFinished,
            durationSec,
            durationString
        )
    }

    companion object {
        const val TAG = "PlaybackMapper"
    }
}