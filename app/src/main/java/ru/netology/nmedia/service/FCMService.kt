package ru.netology.nmedia.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        println(message)
    }

    override fun onNewToken(token: String) {
        println(token)
    }
}