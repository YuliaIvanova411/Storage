package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownAppError
import java.io.IOException

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data: LiveData<List<Post>> = dao.getAll().map(List<PostEntity>::toDto)

    override suspend fun getAll() {
        try {
            val response = ApiService.api.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e : IOException) {
            throw NetworkError
        } catch (e : Exception) {
            throw UnknownAppError
        }
    }




    override suspend fun save(post: Post) {
        try {
            val response = ApiService.api.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e : IOException) {
            throw NetworkError
        } catch (e : Exception) {
            throw UnknownAppError
        }
    }

    override suspend fun removeById(id: Long) {
        val deletedPost = dao.getById(id)
        dao.removeById(id)
        try {
            val response = ApiService.api.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e : IOException) {
            dao.insert(deletedPost)
            throw NetworkError
        } catch (e : Exception) {
            dao.insert(deletedPost)
            throw UnknownAppError
        }
    }


    override suspend fun likeById(id: Long) {
        val likePost = dao.getById(id)
        dao.likeById(likePost.id)
        try {
            val response = ApiService.api.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

        } catch (e : IOException) {
            dao.likeById(likePost.id)
            throw NetworkError
        } catch (e : Exception) {
            dao.likeById(likePost.id)
            throw UnknownAppError
        }
    }

    override suspend fun dislikeById(id: Long) {
        val likePost = dao.getById(id)
        dao.likeById(likePost.id)
        try {
            val response = ApiService.api.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

        } catch (e : IOException) {
            dao.likeById(likePost.id)
            throw NetworkError
        } catch (e : Exception) {
            dao.likeById(likePost.id)
            throw UnknownAppError
        }
    }



    override suspend fun getById(id: Long) {
        dao.getById(id)
        try {
            val response = ApiService.api.getById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }


        } catch (e : IOException) {
            throw NetworkError
        } catch (e : Exception) {
            throw UnknownAppError
        }
    }

  }





