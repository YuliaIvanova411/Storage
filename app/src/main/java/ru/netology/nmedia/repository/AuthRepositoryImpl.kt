package ru.netology.nmedia.repository

import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.model.AuthModel
import java.io.IOException

class AuthRepositoryImpl: AuthRepository {
    override suspend fun login(login: String, password: String): AuthModel {
        try {
            val response = ApiService.api.login(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            return AuthModel(body.id, body.token)
        } catch (e:ApiError) {
            throw ApiError(e.status, e.code)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e:Exception) {
            throw UnknownError()
        }
    }
}