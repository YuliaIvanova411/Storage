package ru.netology.nmedia.auth

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dto.PushToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context
){
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authState: MutableStateFlow<AuthState>
    private val KEY_ID = "id"
    private val KEY_TOKEN = "token"

    init {
        val id = prefs.getLong(KEY_ID, 0)
        val token = prefs.getString(KEY_TOKEN, null)
        if (id == 0L || token == null) {
            _authState = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        }else {
            _authState = MutableStateFlow(AuthState(id,token))
        }
        sendPushToken()
    }
    val authState: StateFlow<AuthState> = _authState.asStateFlow()


    @Synchronized
    fun setAuth(id: Long, token: String){
        _authState.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(KEY_ID, id)
            putString(KEY_TOKEN, token)
            apply()
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _authState.value = AuthState()
        with(prefs.edit()) {
            clear()
            apply()
        }
        sendPushToken()
    }
    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint{
        fun getApiService(): PostApiService
    }

    @SuppressLint("SuspiciousIndentation")
    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
            val tokenDto = token ?: FirebaseMessaging.getInstance().token.await()
            val entryPoint =
                EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
                entryPoint.getApiService().sendPushToken(PushToken(tokenDto))
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }
}
}

data class AuthState(val  id: Long = 0L, val token: String? = null)