package com.laraib.smd_project

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(var co:Context,var msg:String) {
    private val CHANNEL_ID = "massage id"
    private val NOTIFICATION_ID=123
    //set Notification/
    fun Notification(){
        createNotificationChannel()
        val senInt = Intent(co,MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingInt = PendingIntent.getActivity(
            co,
            0,
            senInt,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        //set notification Dialog/
        val icon = BitmapFactory.decodeResource(co.resources,R.drawable.icons8_notification_bell_50)
        val isnotification = NotificationCompat.Builder(co,CHANNEL_ID)
            .setSmallIcon(R.drawable.icons8_notification_bell_50)
            .setLargeIcon(icon)
            .setContentTitle("User Name")
            .setContentText(msg)
            .setContentIntent(pendingInt)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this.co,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(co)
            .notify(NOTIFICATION_ID,isnotification)
    }
    /*create createNotificationChannel*/
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val name = CHANNEL_ID
            val descrip = "Channel descrip"
            val imports = NotificationManager.IMPORTANCE_DEFAULT
            val cannels = NotificationChannel(CHANNEL_ID,name,imports).apply {
                description = descrip
            }
            val notificationManger = co.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManger.createNotificationChannel(cannels)
        }
    }
}