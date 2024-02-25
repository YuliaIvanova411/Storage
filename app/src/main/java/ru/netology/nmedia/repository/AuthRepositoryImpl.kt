package ru.netology.nmedia.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.MediaModel
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
    override suspend fun register(login: String, password: String, name: String): AuthModel {
        try {
            val response = ApiService.api.register(login, password, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            return AuthModel(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun registerWithPhoto(
        login: String,
        password: String,
        name: String,
        media: MediaModel
    ): AuthModel {
        try {
            val part = MultipartBody.Part.createFormData(
                "file", media.file.name, media.file.asRequestBody()
            )
            val loginRequestBody = login.toRequestBody("text/plain".toMediaType())
            val passRequestBody = password.toRequestBody("text/plain".toMediaType())
            val nameRequestBody = login.toRequestBody("text/plain".toMediaType())
            val response = ApiService.api.registerWithPhoto(
                loginRequestBody,
                passRequestBody,
                nameRequestBody,
                part
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            return AuthModel(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}