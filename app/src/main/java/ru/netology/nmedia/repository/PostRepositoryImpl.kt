package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.concurrent.CancellationException
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.enumeration.AttachmentType
import java.io.File

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data: Flow<List<Post>> = dao.getAllVisible()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)
    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000)
            try {
                val response = ApiService.api.getNewer(id)
                val posts = response.body().orEmpty()
                dao.insert(posts.toEntity().map {
                    it.copy(hidden = true)
                })
                emit(posts.size)
            } catch (e: CancellationException) {
                throw e
            }   catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
    override suspend fun showNewPosts() {
        dao.readNew()
    }

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

    override suspend fun saveWithAttachment(post: Post, file: File) {
        try {
            val media = uploadMedia(file)
            val response = ApiService.api.save(post.copy(attachment = Attachment(url = media.id, AttachmentType.IMAGE),))
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }

    private suspend fun uploadMedia(file: File): Media {
        val formData = MultipartBody.Part.createFormData(
            "file", file.name, file.asRequestBody()
        )
        val response = ApiService.api.uploadMedia(formData)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return response.body() ?: throw ApiError(response.code(), response.message())
    }

  }





