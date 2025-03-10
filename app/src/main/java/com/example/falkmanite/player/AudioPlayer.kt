package com.example.falkmanite.player

import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.falkmanite.domain.ProgressState
import com.example.falkmanite.ui.ProgressStateFlow
import javax.inject.Singleton

@Singleton
class AudioPlayer(
    private val appContext: Context,
    private val progressState: ProgressStateFlow
) {

    private var player: MediaPlayer? = null

    private val handler = Handler(Looper.getMainLooper())

    private val updateProgress: Runnable = object : Runnable {
        override fun run() {
//            Log.d(TAG, "run: myProgress = $myProgress")
//            Log.d(TAG, "run: MediaPlayer")
            player?.currentPosition?.let { updateProgress(current = it) }
            handler.postDelayed(this, 100)
        }
    }

    private fun runProgress(run: Boolean) {
        if (run) updateProgress.run() else handler.removeCallbacks(updateProgress)
    }

    private fun updateProgress(current: Int = 0, isFinished: Boolean = false) {
//        Log.d(TAG, "MediaPlayer updateProgress: Progress = $current")
        progressState.update(
            if (isFinished) ProgressState(isFinished = true) else ProgressState(current = current)
        )
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
            updateProgress(isFinished = true)
        }
    }


    fun playTrack() {
//        Log.d(TAG, "play: mp.currentPosition = ${player?.currentPosition}")
//        Log.d(TAG, "play: call")
        player?.start()
        runProgress(true)
    }

    fun pauseTrack() {
        player?.pause()
        runProgress(false)
    }


    fun stopTrack() {
//        Log.d(TAG, "stop: call")
        player?.stop()
        player?.prepare()
        seekTo(0)
        runProgress(false)
    }

    fun seekTo(msec: Int) {
        player?.seekTo(msec)
//        Log.d(TAG, "seekTo: progress seek to ${player?.currentPosition}")
        updateProgress(current = msec)
    }


    private fun destroyPlayer() {
        player?.release()
        player = null
        runProgress(false)
    }

    companion object {
        const val TAG = "AudioMediaPlayer"
    }
}