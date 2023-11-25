package ru.netology.nmedia.holder
 import android.widget.ImageView
import com.bumptech.glide.Glide
 import ru.netology.nmedia.R

fun ImageView.load (url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_load_100dp)
        .error(R.drawable.ic_error_100dp)
        .circleCrop()
        .timeout(10_000)
        .into(this)
}
