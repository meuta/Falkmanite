package com.example.falkmanite.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.falkmanite.domain.usecase.PauseCurrentTrackUseCase
import com.example.falkmanite.service.PlayerService
import com.example.falkmanite.service.PlayerServiceConnection
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StopServiceReceiver : BroadcastReceiver() {

    @Inject
    lateinit var pauseCurrentTrackUseCase: PauseCurrentTrackUseCase

    @Inject
    lateinit var playerServiceConnection: PlayerServiceConnection

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {

            pauseCurrentTrackUseCase()

            playerServiceConnection.unbindService()

            val stopIntent = Intent(it, PlayerService::class.java)
            it.stopService(stopIntent)

            val closeIntent = Intent(Intent.ACTION_MAIN)
            closeIntent.addCategory(Intent.CATEGORY_HOME)
            closeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            it.startActivity(closeIntent)
        }
    }

    companion object {
        private const val TAG = "StopServiceReceiver"
    }
}