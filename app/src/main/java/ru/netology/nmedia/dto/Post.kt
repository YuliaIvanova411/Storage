package ru.netology.nmedia.dto

data class Post (
    val id: Long,
    val author: String,
    var authorAvatar: String,
    var content: String,
    val published: String,
    val likes: Long = 0,
    val likedByMe: Boolean = false,
    val share: Long = 0,
    val videoLink: String? = null,

    )
