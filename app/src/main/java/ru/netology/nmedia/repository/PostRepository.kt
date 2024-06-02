package ru.netology.nmedia.repository

import androidx.paging.PagingData
import ru.netology.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.FeedItem
import java.io.File

interface PostRepository {
    val data : Flow<PagingData<FeedItem>>
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun showNewPosts()
    suspend fun getAll()
    suspend fun getById (id : Long)
    suspend fun likeById (id : Long)
    suspend fun dislikeById (id : Long)
    suspend fun save (post : Post)
    suspend fun removeById (id: Long)
    suspend fun saveWithAttachment(post: Post, file: File)


}