package com.searchimages.presentation.view.search.recycler

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.searchimages.R
import com.searchimages.model.SearchImagesImageData
import kotlinx.android.synthetic.main.recycler_item_search_images.view.iv_images_search_list_item_picture

class SearchImagesViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    fun bind(imageData: SearchImagesImageData?) {
        if (imageData != null) {
            Glide.with(itemView.iv_images_search_list_item_picture.context)
                .load(Uri.parse(imageData.urlMedium))
                .placeholder(ColorDrawable(Color.GRAY)) // TODO: Use pictures
                .error(ColorDrawable(Color.RED)) // TODO: Use pictures
                .into(itemView.iv_images_search_list_item_picture)
        }
    }

    companion object {
        fun create(parent: ViewGroup): SearchImagesViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_search_images, parent, false)
            return SearchImagesViewHolder(view)
        }
    }
}