package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
//    fun getData(): List<Post>
fun getAllAsync(callback: RepositoryCallback<List<Post>>)
fun getByIdAsync(id : Long, callback: RepositoryCallback<Post>)
fun likeByIdAsync(id:Long, callback: RepositoryCallback<Post>)
fun dislikeByIdAsync(id : Long, callback: RepositoryCallback<Post>)
//    fun getById(id : Long) : Post
//    fun shareById(id:Long)
fun removeByIdAsync(id: Long, callback: RepositoryCallback<Unit>)
fun saveAsync(post: Post, callback: RepositoryCallback<Post>)




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