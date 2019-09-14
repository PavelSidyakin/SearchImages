package com.searchimages.presentation.view.search.recycler

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.searchimages.model.SearchImagesImageData

class SearchImagesAdapter : PagedListAdapter<SearchImagesImageData, RecyclerView.ViewHolder>(imagesListDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return SearchImagesViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SearchImagesViewHolder).bind(getItem(position))
    }

    companion object {
        val imagesListDiffCallback = object : DiffUtil.ItemCallback<SearchImagesImageData>() {
            override fun areItemsTheSame(oldItem: SearchImagesImageData, newItem: SearchImagesImageData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SearchImagesImageData, newItem: SearchImagesImageData): Boolean {
                return oldItem == newItem
            }
        }
    }

}