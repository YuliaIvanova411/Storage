package ru.netology.nmedia.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.notify
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject
import kotlin.random.Random
@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
        override fun onMessageReceived(message: RemoteMessage) {
            if(checkRecipientId(message)){
                val recievedMessage = message.data[action]
                if (recievedMessage != null){
                    when (Action.valueOf(recievedMessage)) {
                        Action.LIKE -> handleLike(
                            gson.fromJson(
                                message.data[content], Like::class.java
                            )
                        )
                        Action.NEW_POST -> handleNewPost(
                            gson.fromJson(
                                message.data[content],
                                NewPost::class.java
                            )
                        )
                    }
                } else {
                    defaultNotification()
                }
            }
        }

        override fun onNewToken(token: String) {
            appAuth.sendPushToken(token)
        }

        private fun checkRecipientId(message: RemoteMessage): Boolean {
            val recipientId = gson.fromJson(message.data[content],
            AuthMessage::class.java).recipientId
            val authId = appAuth.authState.value.id
            return when {
                recipientId == authId || recipientId == null -> true
                recipientId == 0L || recipientId != 0L -> {
                    appAuth.sendPushToken(appAuth.authState.value.token)
                    false
                }
                else -> false
            }
        }

        private fun handleLike(content: Like) {
            val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(
                    getString(
                        R.string.notification_user_liked,
                        content.userName,
                        content.postAuthor,
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            notify(notification)
        }

         private fun handleNewPost(content: NewPost){
             val notification = NotificationCompat.Builder(this, channelId)
                 .setSmallIcon(R.drawable.ic_notification)
                 .setContentTitle(
                     getString(
                         R.string.notification_new_post,
                    )
                 )
                 .setStyle(
                     NotificationCompat.BigTextStyle()
                         .bigText(content.content)
                 )
                 .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                 .build()
             notify()
         }
        private fun defaultNotification() {
            val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(
                    getString(
                        R.string.notification_default
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        }

        private fun notify(notification: Notification) {
            if (
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(this)
                    .notify(Random.nextInt(100_000), notification)
            }
        }
    }


    enum class Action {
        LIKE,
        NEW_POST
    }

    data class Like(
        val userId: Long,
        val userName: String,
        val postId: Long,
        val postAuthor: String,
    )
data class NewPost(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val content: String
)

data class AuthMessage(
    val recipientId: Long?,
    val content: String?
)
