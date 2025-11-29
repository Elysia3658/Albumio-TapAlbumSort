package com.example.albumio.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.albumio.databinding.ItemAlbumBinding
import com.example.albumio.logic.data.Album
import com.example.albumio.ui.SortingActivity

class AlbumPagerAdapter : PagingDataAdapter<Album, AlbumPagerAdapter.AlbumViewHolder>(DIFF) {
    class AlbumViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ownAlbum: Album) {
            Glide.with(binding.ImageViewAlbum.context)
                .load(ownAlbum.coverUri)
                .apply(
                    RequestOptions()
                        .transform(CenterCrop(), RoundedCorners(30))
                )
                .into(binding.ImageViewAlbum)
            binding.TextViewAlbumName.text = ownAlbum.name
            binding.TextViewImageNumber.text = ownAlbum.photoCount.toString()

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, SortingActivity::class.java).apply {
                    putExtra("albumId", ownAlbum.id)
                    putExtra("albumName", ownAlbum.name)
                    putExtra("coverUri", ownAlbum.coverUri.toString())
                }
                binding.root.context.startActivity(intent)
            }
        }
    }

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
        ownAlbum?.let { holder.bind(it) }

    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Album>() {
            override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem
        }
    }
}