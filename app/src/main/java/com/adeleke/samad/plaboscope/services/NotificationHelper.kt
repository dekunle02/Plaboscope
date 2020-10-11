package com.adeleke.samad.plaboscope.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.adeleke.samad.plaboscope.*
import com.adeleke.samad.plaboscope.ui.test.TestActivity

class NotificationHelper (val context: Context){

    private val TAG = javaClass.simpleName
    private lateinit var mNotifyManager: NotificationManager


    init {
        createNotificationChannel()
    }


    fun sendRandomTestNotification() {
        val notifyBuilder = generateNotificationBuilder()
        mNotifyManager.notify(RANDOM_NOTIFICATION_ID, notifyBuilder.build())
    }


    private fun generateNotificationBuilder(): NotificationCompat.Builder {
        val mainIntent = generateRandomTestIntent()
        return NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setContentTitle("Plaboscope")
                .setContentText("Take a 5 min test")
                .setSmallIcon(R.drawable.ic_p_logo)
                .setContentIntent(mainIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

    }

    private fun generateRandomTestIntent(): PendingIntent {
        val intent = Intent(context, TestActivity::class.java)
        intent.putExtra(TEST_TYPE_TAG, RANDOM_TEST_TYPE)
        return PendingIntent.getActivity(
                context,
                NOTIFICATION_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }


    private fun createNotificationChannel() {
        mNotifyManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "Plaboscope Notification", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                    context.getString(R.string.notification_channel_description)
            mNotifyManager.createNotificationChannel(notificationChannel)
        }
    }

}