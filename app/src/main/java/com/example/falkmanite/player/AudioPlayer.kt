package com.example.falkmanite.player

import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.Track
import com.example.falkmanite.domain.TrackState
import com.example.falkmanite.ui.ProgressStateFlow
import javax.inject.Singleton

@Singleton
class AudioPlayer(
    private val appContext: Context,
    private val progressState: ProgressStateFlow,
    private val playerState: InMemoryCache<PlayerState>

) {

    private var player: MediaPlayer? = null

    private val handler = Handler(Looper.getMainLooper())

    private val updateProgress: Runnable = object : Runnable {
        override fun run() {
            player?.let {
//                Log.d(TAG, "run: currentPosition = ${it.currentPosition}")
//                Log.d(TAG, "run:        duration = ${it.duration}")
                updateProgress(current = minOf(it.currentPosition, it.duration))
                handler.postDelayed(this, 100)
            }
        }
    }

    private fun runProgress(run: Boolean) {
        if (run) updateProgress.run() else handler.removeCallbacks(updateProgress)
    }


    private fun updateProgress(current: Int = 0) {
        progressState.update(progressState.value().copy(current = current))
    }


    private fun updateProgressFinish() {
        progressState.update(progressState.value().copy(isFinished = true))
    }

    private fun updateDuration(duration: Int?) {
//        Log.d(TAG, "updateDuration: duration = $duration")
        duration?.let { progressState.update(progressState.value().copy(duration = it)) }
    }


    fun playerIsNull() = player == null

    fun isPlaying() = player?.isPlaying ?: false

    fun createPlayer(songId: Int) {
        player?.let { destroyPlayer() }

        val songUri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songId.toLong()
        )

        player = MediaPlayer.create(appContext, songUri)
//        Log.d(TAG, "startPlayer: MediaPlayer created")

        player?.setOnCompletionListener {
            seekTo(0)
            runProgress(false)
            updateProgressFinish()

            playNextTrack()
        }

        updateDuration(player?.duration)
    }


    fun playTrack() {
//        Log.d(TAG, "play: mp.currentPosition = ${player?.currentPosition}")
//        Log.d(TAG, "play: call")
        player?.let {
            it.start()
            runProgress(true)
        }
    }

    fun pauseTrack() {
        player?.let {
            it.pause()
            runProgress(false)
        }
    }

    fun playNextTrack() {

        val state = playerState.read()

        val songIdsList = state.currentPlaylist.songsIds
        val currentSongId = state.currentTrack.id

        val nextSongId = songIdsList.next(currentSongId)

        createPlayer(nextSongId)
        playTrack()

        state.currentTrack = Track(state.songsOfPlaylist.first{ it.id == nextSongId }, TrackState.PLAYING)
        playerState.save(state)
    }

    fun <T> List<T>.next(item: T) = this[(withIndex().first { it.value == item }.index + 1) % size]

    fun stopTrack() {
//        Log.d(TAG, "stop: call")
        player?.let {
            it.stop()
            it.prepare()
            seekTo(0)
            runProgress(false)
        }
    }

    fun seekTo(msec: Int) {
        player?.let {
            it.seekTo(msec)
//            Log.d(TAG, "seekTo: progress seek to ${it.currentPosition}")
            updateProgress(current = msec)
        }
    }


    private fun destroyPlayer() {
        player?.let {
            it.release()
            player = null
            runProgress(false)
        }
    }

    companion object {
        const val TAG = "AudioMediaPlayer"
    }
}