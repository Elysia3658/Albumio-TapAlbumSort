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
import com.example.albumio.logic.data.PhotoAlbum
import com.example.albumio.ui.SortingActivity

class AlbumPagerAdapter : PagingDataAdapter<PhotoAlbum, AlbumPagerAdapter.AlbumViewHolder>(DIFF) {
    class AlbumViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ownPhotoAlbum: PhotoAlbum) {
            Glide.with(binding.ImageViewAlbum.context)
                .asBitmap()
                .load(ownPhotoAlbum.coverPhotoUri)
                .apply(
                    RequestOptions()
                        .transform(CenterCrop(), RoundedCorners(30))
                )
                .into(binding.ImageViewAlbum)
            binding.TextViewAlbumName.text = ownPhotoAlbum.albumName
            binding.TextViewImageNumber.text = ownPhotoAlbum.photoCount.toString()

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, SortingActivity::class.java).apply {
                    putExtra("albumId", ownPhotoAlbum.albumId)
                    putExtra("albumName", ownPhotoAlbum.albumName)
                    putExtra("coverUri", ownPhotoAlbum.coverPhotoUri.toString())
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
        private val DIFF = object : DiffUtil.ItemCallback<PhotoAlbum>() {
            override fun areItemsTheSame(oldItem: PhotoAlbum, newItem: PhotoAlbum) = oldItem.albumId == newItem.albumId
            override fun areContentsTheSame(oldItem: PhotoAlbum, newItem: PhotoAlbum) = oldItem == newItem
        }
    }
}