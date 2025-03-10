package com.example.falkmanite.ui


data class ProgressStateUi(val currentSec: Int, val currentTimeSting: String, val isFinished: Boolean) {
    fun complete(listener: OnTrackCompletionListener) {
        if (isFinished) listener.onTrackCompletion()
    }
}