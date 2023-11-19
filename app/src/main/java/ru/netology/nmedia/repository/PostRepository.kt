package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getData(): List<Post>
    fun likeById(id:Long) : Post
    fun dislikeById(id : Long) :Post
    fun getById(id : Long) : Post
    fun shareById(id:Long)
    fun removeById(id: Long)
    fun save(post: Post)
}