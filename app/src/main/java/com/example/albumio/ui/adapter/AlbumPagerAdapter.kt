package com.example.albumio.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.albumio.databinding.ItemAlbumBinding
import com.example.albumio.logic.data.Album

class AlbumPagerAdapter : PagingDataAdapter<Album, AlbumPagerAdapter.AlbumViewHolder>(DIFF) {
    class AlbumViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val ownAlbum = getItem(position)
        if (ownAlbum != null) {
            Glide.with(holder.binding.ImageViewAlbum.context)
                .load(ownAlbum.coverUri)
                .into(holder.binding.ImageViewAlbum)
            holder.binding.TextViewAlbumName.text = ownAlbum.name
            holder.binding.TextViewImageNumber.text = ownAlbum.photoCount.toString()
        }

    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Album>() {
            override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem
        }
    }
}