package com.example.falkmanite.ui


data class ProgressStateUi(
    val currentPositionSec: Int,
    val currentPositionSting: String,
    val isFinished: Boolean,
    val durationSec: Int,
    val durationString: String
) {
    fun complete(listener: OnTrackCompletionListener) {
        if (isFinished) listener.onTrackCompletion()
    }
}