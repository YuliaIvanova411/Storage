package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nmedia.dto.FeedItem

class FeedItemDiffCallBack : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
    return oldItem.id == newItem.id
}

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean =
     oldItem == newItem
}
