package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.AuthModelState
import ru.netology.nmedia.model.MediaModel
import ru.netology.nmedia.repository.AuthRepository
import ru.netology.nmedia.repository.AuthRepositoryImpl
import java.io.File

class RegisterViewModel: ViewModel() {
    val data = AppAuth.getInstance().authState


    private val repository: AuthRepository = AuthRepositoryImpl()

    private val _state = MutableLiveData<AuthModelState>()
    val state: LiveData<AuthModelState>
        get() = _state

    private val _media = MutableLiveData<MediaModel?>(null)
    val media: LiveData<MediaModel?>
        get() = _media

    fun register(login: String, pass: String, name: String) =
        viewModelScope.launch {
            if (login.isNotBlank() && pass.isNotBlank() && name.isNotBlank()) {
                val avatar = media.value
                if (avatar != null) {
                    try {
                        _state.value = AuthModelState(loading = true)
                        val result = repository.registerWithPhoto(login, pass, name, avatar)
                        AppAuth.getInstance().setAuth(result.id, result.token)
                        _state.value = AuthModelState(loggedIn = true)
                    } catch (e: Exception) {
                        _state.value = AuthModelState(error = true)
                    }
                }
            } else {
                _state.value = AuthModelState(isBlank = true)
            }
            _state.value = AuthModelState()
            clearPhoto()
        }
    fun addPhoto(uri: Uri, file: File){
        _media.value = MediaModel(uri, file)
    }

    fun clearPhoto() {
        _media.value = null
    }
}