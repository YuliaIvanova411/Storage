package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
//    fun getData(): List<Post>
//    fun likeById(id:Long) : Post
//    fun dislikeById(id : Long) :Post
//    fun getById(id : Long) : Post
//    fun shareById(id:Long)
//    fun removeById(id: Long)
//    fun save(post: Post)

    fun getAllAsync(callback: RepositoryCallback<List<Post>>)
    fun getByIdAsync(id: Long, callback: RepositoryCallback<Post>)
    fun likeByIdAsync(id: Long, callback: RepositoryCallback<Post>)
    fun dislikeByIdAsync(id: Long, callback: RepositoryCallback<Post>)
    fun removeByIdAsync(id: Long, callback: RepositoryCallback<Unit>)
    fun saveAsync (post: Post, callback: RepositoryCallback<Post>)
   // fun shareByIdAsync (id: Long, callback: RepositoryCallback<Post>)

    interface RepositoryCallback <T> {
        fun onSuccess(value: T)
        fun onError()
    }
}