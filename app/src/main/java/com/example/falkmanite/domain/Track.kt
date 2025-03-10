package com.example.falkmanite.domain

data class Track(
    val song: Song,
    var trackState: TrackState = TrackState.STOPPED
) {
    val id = song.id
    val duration = song.duration
    fun stop(){
        trackState = TrackState.STOPPED
    }

    fun play() {
        trackState = TrackState.PLAYING
    }

    fun switchPlaying() {
        trackState = when (trackState) {
            TrackState.PLAYING -> TrackState.PAUSED
            else -> TrackState.PLAYING
        }
    }

    fun isPlaying() = trackState == TrackState.PLAYING
}

enum class TrackState {
    PLAYING, PAUSED, STOPPED
}