package ru.netology.nmedia.viewmodel

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dialog.SignOutDialog
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.AuthModelState
import ru.netology.nmedia.repository.AuthRepository
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val repository: AuthRepository
): ViewModel() {
    val data = appAuth

    val authenticated: Boolean
        get() = appAuth.authState.value.id != 0L

    private val _state = MutableLiveData<AuthModelState>()
    val state: LiveData<AuthModelState>
        get() = _state

    fun login(login: String, password: String) = viewModelScope.launch {
        if (login.isNotBlank() && password.isNotBlank()) {
            try {
                _state.value = AuthModelState(loading = true)
                val result = repository.login(login,password)
                appAuth.setAuth(result.id, result.token)
                _state.value = AuthModelState(loggedIn = true)
            } catch (e: Exception) {
                when(e) {
                    is ApiError -> if (e.status == 404) _state.value =
                        AuthModelState(invalidLoginOrPass = true)
                    else -> _state.value = AuthModelState(error = true)
                }
            }
        } else {
            _state.value = AuthModelState(isBlank = true)
        }
        _state.value = AuthModelState()
    }
    fun logout() {
        appAuth.removeAuth()
        _state.value = AuthModelState(notLoggedIn = true)
    }
    fun confirmLogout(manager: FragmentManager) {
        SignOutDialog().show(manager, SignOutDialog.TAG)
    }
}