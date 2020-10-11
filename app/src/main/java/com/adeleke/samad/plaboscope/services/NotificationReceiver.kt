package com.adeleke.samad.plaboscope.services

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.adeleke.samad.plaboscope.RANDOM_TEST_NOTIFICATION_INTENT_ACTION

class NotificationReceiver: BroadcastReceiver() {
    private val TAG = javaClass.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationHelper = NotificationHelper(context!!)
        val intentAction = intent!!.action

        when (intentAction!!) {
            RANDOM_TEST_NOTIFICATION_INTENT_ACTION -> {
                notificationHelper.sendRandomTestNotification()
            }
        }
    }
}