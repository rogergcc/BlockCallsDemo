package com.rogergcc.blockcallsdemo.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_RECEIVER_FOREGROUND
import android.os.Build
import android.util.Log
import com.rogergcc.blockcallsdemo.CallMonitorBroadcastReceiver
import java.util.*


/**
 * Created on agosto.
 * year 2022 .
 */

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val miPendintent = Intent(context, CallMonitorBroadcastReceiver::class.java)
        miPendintent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        miPendintent.flags = Intent.FLAG_RECEIVER_FOREGROUND
        //val now= System.currentTimeMillis()
        Log.e("CallMonitor", "BOOT COMPLETE")
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        try {
            pendingIntent.send()
        } catch (e: CanceledException) {
            e.printStackTrace()
        }

//        val intent2 = Intent(this, CallMonitorBroadcastReceiver::class.java)
//        intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        intent2.setFlags(FLAG_RECEIVER_FOREGROUND);
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_ONE_SHOT)
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager?
//        val now: Long = Calendar.getInstance().timeInMillis
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) alarmManager!!.setExact(
//            AlarmManager.RTC_WAKEUP,
//            now,
//            pendingIntent) else alarmManager!![AlarmManager.RTC_WAKEUP, now] =
//            pendingIntent

//        val service = Intent(context, CallMonitorBroadcastReceiver::class.java)
//        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
//            Log.e("BootComplete","go")
//            context.startService(service)
//        }
    }
}