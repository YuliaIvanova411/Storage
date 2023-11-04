package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import androidx.lifecycle.*
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepositoryImpl

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likes = 0,
    likedByMe = false,
    share = 0,
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )
    val data = repository.getData()

    val edited = MutableLiveData(empty)
    fun likeById(id:Long) = repository.likeById(id)
    fun shareById(id:Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty

    }

    fun edit(post: Post){
        edited.value = post

    }

    fun changeContent(content: String){
        edited.value?.let {post ->
            if (content != post.content) {
                edited.value = post.copy(content = content)
            }

        }

    }
    fun clearEdit() {
        edited.value = empty

    }


}