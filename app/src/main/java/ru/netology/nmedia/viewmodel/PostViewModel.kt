package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.PhotoModel

private val empty = Post(
    id = 0,
    author = "Me",
    authorId = 0,
    authorAvatar = "netology.jpg",
    content = "",
    published = "",
    likes = 0,
    likedByMe = false,
    share = 0,
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    @OptIn(ExperimentalCoroutinesApi::class)
    val data: LiveData<FeedModel> = AppAuth.getInstance()
        .authState
        .flatMapLatest {auth ->
            repository.data.map {
                posts -> FeedModel(
                posts.map { it.copy(ownedByMe = auth.id == it.authorId) },
                posts.isEmpty()
            )
        }
        }
        .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState :LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()

    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

    val newerCount: LiveData<Int> = data.switchMap {
        val id = it.posts.firstOrNull()?.id ?: 0L
        repository.getNewerCount(id)
            .asLiveData(Dispatchers.Default)
    }

    fun setPhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }

    fun clearPhoto(){
        _photo.value = null
    }
    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
                // Начинаем загрузку
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e : Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e : Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
    fun readNew() = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            repository.showNewPosts()
        }
        _dataState.value = FeedModelState()
    }
//    fun like(id : Long) = viewModelScope.launch{
//            val post = repository.getById(id)
//    }

    fun likeById(id: Long) {
        edited.value?.let {
            _postCreated.value = Unit

            viewModelScope.launch {
                try {
                    repository.likeById(id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun disLikeById(id : Long) {
         edited.value?.let {

             _postCreated.value = Unit
             viewModelScope.launch {
                 try {
                     repository.dislikeById(id)
                     _dataState.value = FeedModelState()
                 } catch (e: Exception) {
                     _dataState.value = FeedModelState(error = true)
                 }
             }
         }
         edited.value = empty
     }





//    }
    fun removeById(id: Long) {
    //val old = _data.value?.posts.orEmpty()
    viewModelScope.launch{
        try{
            repository.removeById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)

        }
    }
    }



    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    _photo.value?.let{ photoModel ->
                        repository.saveWithAttachment(it, photoModel.file)
                    } ?: run {
                        repository.save(it)
                    }
                    _dataState.value = FeedModelState()
                } catch (e : Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
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

}


