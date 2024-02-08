package ru.netology.nmedia.repository

import ru.netology.nmedia.model.AuthModel

interface AuthRepository {
    suspend fun login(login: String, password: String): AuthModel
}