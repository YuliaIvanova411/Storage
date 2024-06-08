package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

sealed interface FeedItem {
    val id: Long
}

data class Post (
    override val id: Long,
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
    val ownedByMe: Boolean = false
    ) : FeedItem
data class Ad(
    override val id: Long,
    val image: String,
) : FeedItem
data class Attachment(
    val url: String,
    val type: AttachmentType,
)
