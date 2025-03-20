package com.example.falkmanite.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerServiceConnection @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var playerService: PlayerService? = null
    private var isBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val serviceBinder = binder as PlayerService.PlayerBinder
            playerService = serviceBinder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
            isBound = false
        }
    }


    fun startAndBindService() {
        if (!isBound) {
            val intent = Intent(context, PlayerService::class.java)
            ContextCompat.startForegroundService(context, intent)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

    fun getService(): PlayerService? = playerService

    companion object {
        private const val TAG = "PlayerServiceConnection"
    }
}
