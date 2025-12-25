package com.example.albumio.myClass

import android.net.Uri
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.albumio.databinding.ItemPhotoBinding


class PhotoPagerAdapter
    : PagingDataAdapter<Uri, PhotoPagerAdapter.PhotoViewHolder>(DIFF) {
    class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root){
        fun photoView() {
            binding.photoView.setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
                override fun onDoubleTap(p0: MotionEvent): Boolean {
                    return true
                }

                override fun onDoubleTapEvent(p0: MotionEvent): Boolean {
                    return true
                }

                override fun onSingleTapConfirmed(p0: MotionEvent): Boolean {
                    return false
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val uri = getItem(position)
        Glide.with(holder.binding.photoView.context)
            .load(uri)
            .into(holder.binding.photoView)
        holder.photoView()
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Uri>() {
            override fun areItemsTheSame(oldItem: Uri, newItem: Uri) = oldItem == newItem
            override fun areContentsTheSame(oldItem: Uri, newItem: Uri) = oldItem == newItem
        }
    }
}