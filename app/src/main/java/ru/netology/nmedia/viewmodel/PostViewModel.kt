package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import androidx.lifecycle.*
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

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
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data

    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()

    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            // Начинаем загрузку
            _data.postValue(FeedModel(loading = true))
            try {
                // Данные успешно получены
                val posts = repository.getData()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                // Получена ошибка
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun likeById(id: Long) {
        thread {
            _data.postValue(
                FeedModel(posts =
                _data.value!!.posts.map { post ->
                    if (post.id == id) {
                        post.copy(
                            likes = if (post.likedByMe) post.likes - 1 else post.likes + 1,
                            likedByMe = !post.likedByMe
                        )
                    } else {
                        post
                    }
                })
            )
            repository.likeById(id) }
    }

    fun shareById(id: Long) {
        _data.postValue(
            FeedModel(posts =
            _data.value!!.posts.map { post ->
                if (post.id == id) {
                    post.copy(
                        share = post.share + 1
                    )
                } else {
                    post
                }
            })
        )

     repository.shareById(id)

    }
    fun removeById(id: Long) {
        thread {
            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }


    fun save() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty

    }

    fun edit(post: Post) {
        edited.value = post

    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun clearEdit() {
        edited.value = empty

    }
    fun getById(id: Long) {
        thread {
            repository.getById(id)
        }
    }
}


