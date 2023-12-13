package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data : LiveData<List<Post>>
    suspend fun getAll()
    suspend fun getById (id : Long)
    suspend fun likeById (id : Long)
    suspend fun dislikeById (id : Long)
    suspend fun save (post : Post)
    suspend fun removeById (id: Long)




//    fun getByIdAsync(id: Long, callback: RepositoryCallback<Post>)
//    fun likeByIdAsync(id: Long, callback: RepositoryCallback<Post>)
//    fun dislikeByIdAsync(id: Long, callback: RepositoryCallback<Post>)
//    fun removeByIdAsync(id: Long, callback: RepositoryCallback<Unit>)
//    fun saveAsync (post: Post, callback: RepositoryCallback<Post>)
//   // fun shareByIdAsync (id: Long, callback: RepositoryCallback<Post>)

    interface RepositoryCallback <T> {
        fun onSuccess(value: T)
        fun onError(e: Exception)
    }
}