package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post (
    val id: Long,
    val author: String,
    val authorId: Long,
    var authorAvatar: String,
    var content: String,
    val published: String,
    val likes: Long = 0,
    val likedByMe: Boolean = false,
    val share: Long = 0,
    val videoLink: String? = null,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = true

    )
data class Attachment(
    val url: String,
    //   val description: String?,
    val type: AttachmentType,
)
