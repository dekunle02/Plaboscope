package com.adeleke.samad.plaboscope.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.text.format.DateUtils
import com.adeleke.samad.plaboscope.NOTIFICATION_REQUEST_CODE
import com.adeleke.samad.plaboscope.RANDOM_TEST_NOTIFICATION_INTENT_ACTION

class AlarmHelper(val context: Context) {
    private val TAG = javaClass.simpleName
    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setAlarm(type: String) {

        val triggerTime = AlarmManager.ELAPSED_REALTIME_WAKEUP
        val repeatInterval = when (type) {
            "daily" -> AlarmManager.INTERVAL_DAY
            "twiceDaily" -> AlarmManager.INTERVAL_HALF_DAY
            "weekly" -> DateUtils.WEEK_IN_MILLIS
            else -> DateUtils.DAY_IN_MILLIS * 2
        }
        val broadcastReceiverIntent = Intent(context, NotificationReceiver::class.java)
        broadcastReceiverIntent.action = RANDOM_TEST_NOTIFICATION_INTENT_ACTION
        val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_REQUEST_CODE,
                broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime + repeatInterval,
                repeatInterval,
                pendingIntent
        )
//        alarmManager.setInexactRepeating(
//                AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime(),
//                5000,
//                pendingIntent
//        )
    }
    fun cancelAlarms() {
        val broadcastReceiverIntent = Intent(context, NotificationReceiver::class.java)
        broadcastReceiverIntent.action = RANDOM_TEST_NOTIFICATION_INTENT_ACTION
        val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_REQUEST_CODE,
                broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

}