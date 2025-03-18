package com.example.falkmanite.domain

import com.example.falkmanite.ui.ProgressMapper
import com.example.falkmanite.ui.ProgressStateUi

data class ProgressState(val current: Int = 0, val isFinished: Boolean = false, val duration: Int = 0) {
    fun map(mapper: ProgressMapper): ProgressStateUi {
        return mapper(this)
    }
}