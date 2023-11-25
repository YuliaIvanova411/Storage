package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    var content: String,
    val published: String,
    val likes: Long = 0,
    val likedByMe: Boolean = false,
    val share: Long = 0,
    val videoLink: String? = null,
) {
    fun toDto() = Post(id,author,authorAvatar,content,published,likes,likedByMe,share,videoLink)

    companion object {
        fun fromDto(dto: Post) =
        PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likes, dto.likedByMe, dto.share, dto.videoLink)

    }
}