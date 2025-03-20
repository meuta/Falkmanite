package com.example.falkmanite.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.falkmanite.FalkmaniteApp.Companion.CHANNEL_ID
import com.example.falkmanite.MainActivity
import com.example.falkmanite.R
import com.example.falkmanite.broadcastReceiver.StopServiceReceiver
import com.example.falkmanite.domain.usecase.PauseCurrentTrackUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import androidx.media.app.NotificationCompat as MediaNotificationCompat



@AndroidEntryPoint
class PlayerService : Service() {

    @Inject
    lateinit var pauseCurrentTrackUseCase: PauseCurrentTrackUseCase

    private val binder = PlayerBinder()

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }


    private fun createNotification(): Notification {
        val returnToActivityIntent = Intent(this, MainActivity::class.java)

        val returnToActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            returnToActivityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, StopServiceReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(returnToActivityPendingIntent)
            .addAction(R.drawable.baseline_close_24, "", stopPendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setStyle(MediaNotificationCompat.MediaStyle())


        return notificationBuilder.build()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                createNotification(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                } else {
                    0
                }
            )


        return START_NOT_STICKY
    }



    companion object {
        private const val TAG = "PlayerService"

        private const val NOTIFICATION_ID = 1
    }
}