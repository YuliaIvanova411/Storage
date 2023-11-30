package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import androidx.lifecycle.*
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import java.net.ConnectException

private val empty = Post(
    id = 0,
    author = "Me",
    authorAvatar = "netology.jpg",
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
                // Начинаем загрузку
            _data.postValue(FeedModel(loading = true))
            repository.getAllAsync(object : PostRepository.RepositoryCallback<List<Post>> {
                override fun onSuccess(value: List<Post>) {
                    _data.value = FeedModel(posts = value, empty = value.isEmpty())
                }

                override fun onError(e: Exception) {
                    _data.postValue(if (e is ConnectException) FeedModel(connectError = true)
                    else FeedModel(error = true))
                }
            })

    }
    fun like(id : Long) {

        repository.getByIdAsync(id, object : PostRepository.RepositoryCallback<Post> {
            override fun onError(e: Exception) {
                _data.postValue(
                    if (e is ConnectException) FeedModel(connectError = true)
                    else FeedModel(error = true)
                )
            }

            override fun onSuccess(post: Post) {
                if (post.likedByMe) disLikeById(id) else likeById(id)
            }
        })
    }

    private fun likeById(id: Long) {
        repository.likeByIdAsync(id, object : PostRepository.RepositoryCallback<Post>{

            override fun onSuccess(value: Post) {
                _data.postValue(
                    FeedModel(posts =
                    _data.value?.posts?.map { post ->
                        if (post.id == id) {
                            value
                        } else {
                            post
                        }
                    } ?: emptyList())
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(
                    if (e is ConnectException) FeedModel(connectError = true)
                    else FeedModel(error = true)
                )
            }
        })
    }
     private fun disLikeById(id : Long) {
        repository.dislikeByIdAsync(id, object : PostRepository.RepositoryCallback<Post> {
            override fun onError(e: Exception) {
                _data.postValue(
                    if (e is ConnectException) FeedModel(connectError = true)
                    else FeedModel(error = true)
                )
            }

            override fun onSuccess(value: Post) {
                _data.postValue(
                    FeedModel(posts =
                    _data.value?.posts?.map {
                        if (value.id == it.id) {
                            value
                        } else {
                            it
                        }
                    } ?: emptyList())
                )
            }

        })

}



//    fun shareById(id: Long) {
//        _data.postValue(
//            FeedModel(posts =
//            _data.value!!.posts.map { post ->
//                if (post.id == id) {
//                    post.copy(
//                        share = post.share + 1
//                    )
//                } else {
//                    post
//                }
//            })
//        )
//
//     repository.shareById(id)
//
//    }
    fun removeById(id: Long) {
    //val old = _data.value?.posts.orEmpty()
       repository.removeByIdAsync(id, object : PostRepository.RepositoryCallback<Unit> {

           override fun onError(e: Exception) {
               _data.postValue(
                   if (e is ConnectException) FeedModel(connectError = true)
                   else FeedModel(error = true)
               )
           }
           override fun onSuccess(value: Unit) {
               try {
                   _data.postValue(_data.value?.copy(posts = _data.value?.posts.orEmpty()
                       .filter { it.id != id })
                   )

               } catch (e: IOException) {
                   _data.postValue(FeedModel(error = true))
               //_data.postValue(_data.value?.copy(posts = old))
               }
           }
        })
    }



    fun save() {
        edited.value?.let {
                repository.saveAsync(it, object : PostRepository.RepositoryCallback<Post> {

                    override fun onSuccess(value: Post) {
                        _data.postValue(FeedModel())
                        _postCreated.postValue(Unit)
                        loadPosts()
                    }

                    override fun onError(e: Exception) {
                        _data.postValue(
                            if (e is ConnectException) FeedModel(connectError = true)
                            else FeedModel(error = true)
                        )
                    }

                })
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
//    fun getById(id: Long) {
//        thread {
//            repository.getById(id)
//        }
//    }
}


