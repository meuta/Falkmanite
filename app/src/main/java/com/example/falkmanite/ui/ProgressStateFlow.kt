package com.example.falkmanite.ui

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.example.falkmanite.domain.ProgressState
import javax.inject.Singleton

@Singleton
class ProgressStateFlow {

    private val stateFlow: MutableStateFlow<ProgressState> = MutableStateFlow(ProgressState())

    private val scope = CoroutineScope(Dispatchers.Main)

    fun update(value: ProgressState){ stateFlow.value = value }

    fun map(mapper: ProgressMapper): StateFlow<ProgressStateUi> {
        return stateFlow
            .map { mapper(it) }
            .stateIn(scope, SharingStarted.Lazily, mapper(ProgressState()))
    }

    fun value() = stateFlow.value

    companion object {
        private const val TAG = "ProgressStateFlow"
    }
}