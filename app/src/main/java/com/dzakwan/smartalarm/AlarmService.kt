package com.dzakwan.smartalarm

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.*

class AlarmService : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra(EXTRA_MESSAGE)
        val type = intent?.getIntExtra(EXTRA_TYPE,0)

        val title = when (type) {
            TYPE_ONE_TIME -> "One Time Alarm"
            TYPE_REPEATING -> "Repeating Alarm"
            else -> "Something wrong"
        }
        val requestCode = when(type) {
            TYPE_ONE_TIME -> ID_ONE_TIME
            TYPE_REPEATING -> ID_REPEATING
            else -> -1
        }

        if (message != null && context != null) {
                showNotificationAlarm(
                    context,
                    title,
                    "hai Dzakwan, its time for $message, Good Luck",
                    requestCode,
                )
            }
        }

    fun cancelAlarm(context: Context, type: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmService::class.java)
        val requestCode = when(type){
            TYPE_ONE_TIME -> ID_ONE_TIME
            TYPE_REPEATING -> ID_REPEATING
            else -> Log.d("CancelAlarm", "Unknown type of Alarm")
        }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)
        if (type == TYPE_ONE_TIME) {
            Toast.makeText(context, "One Time Alarm Canceled.", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(context, "Repeating Alarm Canceled.", Toast.LENGTH_SHORT).show()
        }
    }

    fun setRepeatingAlarm(context: Context, type: Int, time: String, note: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmService::class.java)
        intent.putExtra(EXTRA_MESSAGE, note)
        intent.putExtra(EXTRA_TYPE, note)

        //2-2-2022
        // setelah di split -> 2 2 2022
        // diconvert menjadi array -> [2, 2, 2022]
//        val dateArray = date.split("-").toTypedArray()
        val timeArray = time.split(":").toTypedArray()

        val calendar = Calendar.getInstance()
//        //date
//        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]))
//        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1])-1)
//        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[0]))

        //Time
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent,0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context,"Succes set RepeatingAlarm", Toast.LENGTH_SHORT).show()
        Log.i("SetAlarmRinging", "setRepeatingAlarm: ${calendar.time}")
    }

    fun setOneTimeAlarm(context: Context, type: Int, date: String, time: String, note: String){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmService::class.java)
        intent.putExtra(EXTRA_MESSAGE, note)
        intent.putExtra(EXTRA_TYPE, note)

        //2-2-2022
        // setelah di split -> 2 2 2022
        // diconvert menjadi array -> [2, 2, 2022]
        val dateArray = date.split("-").toTypedArray()
        val timeArray = time.split(":").toTypedArray()

        val calendar = Calendar.getInstance()
        //date
        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]))
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1])-1)
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[0]))

        //Time
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_ONE_TIME, intent,0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context,"Succes set OneTimeAlarm", Toast.LENGTH_SHORT).show()
        Log.i("SetAlarmRinging", "setOneTimeAlarm: ${calendar.time}")
    }


    private fun showNotificationAlarm(
        context: Context,
        title: String,
        note: String,
        notificationId: Int,
    ) {
        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "smart_alarm"

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_one_time)
            .setContentTitle(title)
            .setContentText(note)
            .setSound(ringtone)


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, "Smart Alarm", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(channelId)
            notificationManager.createNotificationChannel(channel)
        }
        val notif = builder.build()
        notificationManager.notify(notificationId, notif)
    }

    companion object{
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_TYPE = "type"

        const val ID_ONE_TIME = 101
        const val ID_REPEATING = 102

        const val TYPE_ONE_TIME = 1
        const val TYPE_REPEATING = 0
    }

}

