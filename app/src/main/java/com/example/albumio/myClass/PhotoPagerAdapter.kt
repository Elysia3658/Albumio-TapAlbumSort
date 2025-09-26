package com.example.albumio.myClass

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.albumio.databinding.ItemPhotoBinding

class PhotoPagerAdapter(
    private val uris: List<Uri>
) : RecyclerView.Adapter<PhotoPagerAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val uri = uris[position]
        Glide.with(holder.binding.photoView.context)
            .load(uri)
            .into(holder.binding.photoView)
    }

    override fun getItemCount() = uris.size
}