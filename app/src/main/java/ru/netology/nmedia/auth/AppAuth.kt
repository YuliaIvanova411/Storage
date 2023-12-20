package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.dto.Token

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow<Token?>(null)
    val state = _state.asStateFlow()

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0)
        if (!prefs.contains(ID_KEY) || token == null) {
            prefs.edit { clear() }
        } else {
            _state.value = Token(id, token)
        }
    }
    @Synchronized
    fun  setAuth(id: Long, token: String) {
        prefs.edit{
            putString(TOKEN_KEY, token)
            putLong(ID_KEY, id)
        }
        _state.value = Token(id, token)
    }
    @Synchronized
    fun clearAuth() {
        prefs.edit { clear()}
        _state.value = null
    }
    companion object {

        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"

        private var INSTANCE : AppAuth? = null

        fun getInstance() :AppAuth = requireNotNull(INSTANCE)
        fun initApp(context: Context){
            INSTANCE = AppAuth(context)

        }
    }
}