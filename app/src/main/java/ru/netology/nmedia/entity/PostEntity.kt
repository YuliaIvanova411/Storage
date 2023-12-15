package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import androidx.room.Embedded
import ru.netology.nmedia.dto.Attachment
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
    val hidden: Boolean = false,
    @Embedded
    val attachment: Attachment?,
) {
    fun toDto() = Post(
        id= id,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likes = likes,
        likedByMe = likedByMe,
        share = share,
        videoLink = videoLink,
        attachment = attachment)

    companion object {
        fun fromDto(dto: Post) =
        PostEntity(
            id = dto.id,
            author = dto.author,
            authorAvatar = dto.authorAvatar,
            content = dto.content,
            published = dto.published,
            likes = dto.likes,
            likedByMe = dto.likedByMe,
            share = dto.share,
            videoLink = dto.videoLink,
            attachment = dto.attachment)

    }
}
    fun List<PostEntity>.toDto() : List<Post> = map(PostEntity::toDto)
    fun List<Post>.toEntity() : List<PostEntity> = map(PostEntity::fromDto)
