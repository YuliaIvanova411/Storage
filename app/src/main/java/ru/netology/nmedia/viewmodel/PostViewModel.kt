package ru.netology.nmedia.viewmodel


import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.util.SingleLiveEvent
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dialog.SignInDialog
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.model.PhotoModel
import javax.inject.Inject

private val empty = Post(
    id = 0,
    author = "Me",
    authorId = 0L,
    authorAvatar = "netology.jpg",
    content = "",
    published = "",
    likes = 0,
    likedByMe = false,
    share = 0,
)
@HiltViewModel
@ExperimentalCoroutinesApi
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
    ) : ViewModel() {

    val data: Flow<PagingData<FeedItem>> = appAuth.authState
        .flatMapLatest { (myId, _) ->
            repository.data.map {
                posts ->
                posts.map {post ->
                    if (post is Post) {
                    post.copy(ownedByMe = post.authorId == myId)
                    } else {
                        post
                    }}
        }
        }
        .flowOn(Dispatchers.Default)

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

    fun setPhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }

    fun clearPhoto(){
        _photo.value = null
    }
    init {
        loadPosts()
    }
    fun isAuthorized(manager: FragmentManager): Boolean {
        return if (appAuth.authState.value != null) {
            true
        } else {
            SignInDialog().show(manager, SignInDialog.TAG)
            false
        }
    }
    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
                //repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e : Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
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

    fun removeById(id: Long) {
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


