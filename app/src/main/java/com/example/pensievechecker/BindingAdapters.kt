package com.example.pensievechecker

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {
    imageUrl?.let {
        Glide.with(view.context)
            .load(it)
            .apply(RequestOptions().placeholder(android.R.drawable.progress_indeterminate_horizontal).error(android.R.drawable.stat_notify_error))
            .into(view)
    }
}
