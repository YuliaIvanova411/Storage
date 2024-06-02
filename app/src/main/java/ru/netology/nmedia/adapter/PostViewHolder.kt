package ru.netology.nmedia.adapter


import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.holder.load
import ru.netology.nmedia.holder.loadAttachment

interface PostListener {
    fun onRemove(post: Post)
    fun onEdit (post: Post)
    fun onLike (post: Post)

    fun onAttachment(post: Post)

   // fun onShare (post: Post)

    fun onPlay(post: Post)
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: PostListener,
) : ViewHolder(binding.root) {

    private val attachmentUrl = "${BuildConfig.BASE_URL}/media/"
    fun count(number: Long) = when (number) {
        in 0..999 -> number.toString()
        in 1000..9_999
        -> (number / 1_000).toString() + "," + ((number % 1_000) / 100).toString() + "K"

        in 10_000..999_999
        -> (number / 1_000).toString() + "K"

        in 1_000_000..99_999_999
        -> (number / 1_000_000).toString() + "," + ((number % 1_000_000) / 100_000).toString() + "M"

        else -> "wtf"
    }

    fun bind(post: Post) {
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = (count(post.likes))
            share.text = (count(post.share))

            menu.isVisible = post.ownedByMe


            val url = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"

            binding.avatar.load(url)

            if(post.videoLink != null) {
                videoLayout.visibility = View.VISIBLE

            } else {
                videoLayout.visibility = View.GONE

            }

            videoLayout.setOnClickListener {
                listener.onPlay(post)
            }

            like.setOnClickListener {
                listener.onLike(post)
            }

//            share.setOnClickListener {
//                listener.onShare(post)
//            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(post)
                                true
                            }
                            R.id.edit ->{
                                listener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }
                    .show()
            }
            if (post.attachment != null) {
                attachment.visibility = View.VISIBLE
                attachment.loadAttachment(attachmentUrl + post.attachment.url)
            } else {
                attachment.visibility = View.GONE
            }

            attachment.setOnClickListener {
                listener.onAttachment(post)
            }
        }
    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
): ViewHolder(binding.root) {
    fun bind(ad:Ad) {
        binding.image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
    }
}