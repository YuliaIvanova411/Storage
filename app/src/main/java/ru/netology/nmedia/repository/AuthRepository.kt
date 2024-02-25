package ru.netology.nmedia.repository

import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.MediaModel

interface AuthRepository {
    suspend fun login(login: String, password: String): AuthModel
    suspend fun register(login: String, password: String, name: String) : AuthModel
    suspend fun registerWithPhoto(login: String, password: String,
                                  name: String, media: MediaModel
    ) : AuthModel
}