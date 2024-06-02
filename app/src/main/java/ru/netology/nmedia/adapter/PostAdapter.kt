package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post


class PostAdapter(
    private val listener: PostListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallBack()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("unknown item type")
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
       when (viewType) {
           R.layout.card_post -> {

        val binding =
            CardPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        PostViewHolder(
            binding = binding,
            listener = listener,
        )
    }
           R.layout.card_ad -> {
               val binding =
                   CardAdBinding.inflate(
                       LayoutInflater.from(parent.context),
                       parent,
                       false)
               AdViewHolder(binding)

           }
        else -> error("unknown view type: $viewType")
       }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (it) {
                is Post -> (holder as? PostViewHolder)?.bind(it)
                is Ad -> (holder as? AdViewHolder)?.bind(it)
                null -> error("unknown item type")
            }
        }
    }

}