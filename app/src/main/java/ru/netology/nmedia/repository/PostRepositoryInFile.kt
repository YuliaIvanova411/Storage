package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryInFile(
    private val context: Context,
) : PostRepository {

    companion object {

        private const val FILE_NAME = "posts.json"
    }

    private var nextId = 1L
    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private var post: List<Post> = readPosts()
        set(value) {
            field = value
            sync()
        }

    private val data = MutableLiveData(post)


    override fun getData(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        post = post.map { post ->
            if (post.id == id) {
                post.copy(
                    likedByMe = !post.likedByMe, likes = if
                        (post.likedByMe) post.likes - 1 else
                        post.likes + 1
                )
            } else {
                post
            }
        }

        data.value = post
    }

    override fun shareById(id: Long) {
        post = post.map { post ->
            if (post.id == id) {
                post.copy(share = post.share + 1)
            } else {
                post
            }
        }
        data.value = post
    }

    override fun removeById(id: Long) {
        post = post.filter {
            it.id != id
        }
        data.value = post
    }

    override fun save(newPost: Post) {
        if (newPost.id == 0L) {
            post = listOf(
                newPost.copy(
                    id = nextId++,
                    author = "Me",
                    likedByMe = false,
                    published = "Now",
                )
            ) + post
            data.value = post
            return
        }
        post = post.map {
            if (it.id != newPost.id) it else it.copy(content = newPost.content)
        }
        data.value = post
    }

    private fun sync() {
        context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(post))
        }
    }

    private fun readPosts(): List<Post> {
        val file = context.filesDir.resolve(FILE_NAME)
        return if (file.exists()) {
            context.openFileInput(FILE_NAME).bufferedReader().use {
                gson.fromJson(it, type)
            }
        } else {
            emptyList()
        }
    }
}





