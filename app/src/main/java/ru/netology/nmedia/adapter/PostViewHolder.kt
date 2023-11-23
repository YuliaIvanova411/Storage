package ru.netology.nmedia.adapter

import android.net.Uri
import android.view.View
import android.widget.PopupMenu
import androidx.navigation.fragment.NavHostFragment


import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.coroutines.NonCancellable.start
import ru.netology.nmedia.FeedFragment
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

interface PostListener {
    fun onRemove(post: Post)
    fun onEdit (post: Post)
    fun onLike (post: Post)

   // fun onShare (post: Post)

    fun onPlay(post: Post)
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: PostListener,
) : ViewHolder(binding.root) {
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
        }
    }
}