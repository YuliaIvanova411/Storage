package ru.netology.nmedia.auth

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.PushToken
import java.lang.IllegalStateException

class AppAuth private constructor(context: Context){
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authState: MutableStateFlow<AuthState>

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
    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
            val tokenDto = PushToken(token ?: FirebaseMessaging.getInstance().token.await())
            ApiService.api.sendPushToken(tokenDto)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }
}
    companion object {

        private const val KEY_ID = "id"
        private const val KEY_TOKEN = "token"
        @Volatile
        private var instance: AppAuth? = null

        fun getInstance() = synchronized(this) {
            instance ?: throw IllegalStateException ("getInstance should be called only after initAuth")
        }

        fun initAuth(context: Context) = instance ?: synchronized(this) {
            instance ?: AppAuth(context).also { instance = it}
        }
    }
}

data class AuthState(val  id: Long = 0L, val token: String? = null)