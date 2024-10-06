package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val CHANNEL_ID = "1"
    private var counter = 0

    lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainBinding.sendNotification.setOnClickListener {

            counter++

            mainBinding.sendNotification.text = counter.toString()

//            if(counter % 5 == 0){
                startNotification()
//            }

        }

    }

    fun startNotification(){

        val intent = Intent(applicationContext,MainActivity::class.java)

        val pendingIntent = if(Build.VERSION.SDK_INT>=23) {
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }else {
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }

        //actionButton
        val actionIntent = Intent(applicationContext,Receiver::class.java)
        actionIntent.putExtra("toast", "This is notification Message")

        val actionPending = if(Build.VERSION.SDK_INT>=23) {
            PendingIntent.getBroadcast(
                applicationContext,
                1,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }else {
            PendingIntent.getBroadcast(
                applicationContext,
                1,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }

        //dismissButton
        val dismissIntent = Intent(applicationContext,ReceiverDismiss::class.java)

        val dismissPending = if(Build.VERSION.SDK_INT>=23) {
            PendingIntent.getBroadcast(
                applicationContext,
                2,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }else {
            PendingIntent.getBroadcast(
                applicationContext,
                2,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(this@MainActivity,CHANNEL_ID,)

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID,"1",NotificationManager.IMPORTANCE_DEFAULT)
            val manager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            builder.setSmallIcon(R.drawable.small_icon)
                .setContentTitle("Title")
                .setContentText(counter.toString())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.small_icon, "Toast Message", actionPending)
                .addAction(R.drawable.small_icon, "Dismiss", dismissPending)
        }else{
            builder.setSmallIcon(R.drawable.small_icon)
                .setContentTitle("Notification Title")
                .setContentText("This is the notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.small_icon, "Toast Message", actionPending)
                .addAction(R.drawable.small_icon, "Dismiss", dismissPending)

        }

        val notificationManagerCompat = NotificationManagerCompat.from(this@MainActivity)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),100)
            }
            return
        }
        notificationManagerCompat.notify(1,builder.build())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==100){
            if(grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startNotification()
            }
        }
    }
}