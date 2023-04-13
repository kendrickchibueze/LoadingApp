package com.example.loadingapp

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var ID: Long = 0
    private var URL = ""
    private var pName=""
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


        custom_button.setOnClickListener {
            when {
                Button1.isChecked -> {
                    URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
                    pName = "Glide Project"
                }
                Button2.isChecked -> {
                    pName = "Load app project"
                    URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
                }
                Button3.isChecked -> {
                    pName = "Retrofit Project"
                    URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
                }
                else -> {
                    Toast.makeText(this, "Please select file", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            download()
            custom_button.buttonState = LoadingButton.ButtonState.Loading
        }

    }



    private fun setDownloadURL() {
        URL = when {
            Button1.isChecked -> "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
            Button2.isChecked -> "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
            Button3.isChecked -> "https://github.com/square/retrofit/archive/refs/heads/master.zip"
            else -> ""
        }
        pName = when {
            Button1.isChecked -> "Glide Project"
            Button2.isChecked -> "Load app project"
            Button3.isChecked -> "Retrofit Project"
            else -> ""
        }
    }


    private val receiver = DownloadReceiver(::createNotifications, ::onDownloadComplete)

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                //.setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        ID = downloadManager.enqueue(request)
    }

    private fun createNotifications(status: String) {
        val notification = Notification(status, pName)
        val notificationSender = NotificationSender(applicationContext, notification)
        notificationSender.sendNotification()
    }

    @SuppressLint("Range")
    private fun onDownloadComplete(id: Long) {
        custom_button.buttonState = LoadingButton.ButtonState.Completed

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(id)
        val cursor = downloadManager.query(query)

        if(cursor.moveToFirst()){
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if(status==DownloadManager.STATUS_FAILED){
                createNotifications("Failed")
            } else if(status==DownloadManager.STATUS_SUCCESSFUL){
                createNotifications("successful")
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "channelId"
    }
}

class NotificationSender(private val context: Context, private val notification: Notification) {
    fun sendNotification() {
        createChannel("channel_id", "notification_channel")
        val contentIntent = Intent(context, DetailActivity::class.java).apply {
            putExtra("status", notification.status)
            putExtra("filename", notification.projectName)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            123,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            context,
            "channel_id"
        )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentIntent(contentPendingIntent)
            .setContentText("${notification.projectName} is downloaded")
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "View Details",
                contentPendingIntent
            )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Notification channel description"
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

data class Notification(val status: String, val projectName: String)

class DownloadReceiver(
    private val createNotification: (String) -> Unit,
    private val onDownloadComplete: (Long) -> Unit
) : BroadcastReceiver() {

    @SuppressLint("Range")
    override fun onReceive(context: Context? , intent: Intent?) {
        val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (id != null && id != -1L) {
            onDownloadComplete(id)
            val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterById(id)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    createNotification("Download Successful")
                } else {
                    createNotification("Download Failed")
                }
            }
        }
    }
}
