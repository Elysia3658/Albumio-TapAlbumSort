package com.example.albumio.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.albumio.databinding.ItemImageMovesButtonsBinding
import com.example.albumio.logic.data.PhotoAlbum

class ImageMovesButtonsAdapter : ListAdapter<PhotoAlbum,ImageMovesButtonsAdapter.ButtonsViewHolder>(DIFF) {
    inner class ButtonsViewHolder(val binding: ItemImageMovesButtonsBinding) : RecyclerView.ViewHolder(binding.root){
        val albumName = binding.nameAlbum
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonsViewHolder {
        val binding = ItemImageMovesButtonsBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ButtonsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ButtonsViewHolder, position: Int) {
        val place = getItem(position)
        holder.albumName.text = place.albumName

        val layoutParams = holder.itemView.layoutParams
        val screenWidth = holder.itemView.resources.displayMetrics.widthPixels - 50  //TODO：这里的50是为了预留间距，其他的适配包有问题的
        layoutParams.width = screenWidth / 5
        holder.itemView.layoutParams = layoutParams
        // TODO：这里可以进行多设备配置多个

    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<PhotoAlbum>() {
            override fun areItemsTheSame(oldItem: PhotoAlbum, newItem: PhotoAlbum) = oldItem.albumId == newItem.albumId
            override fun areContentsTheSame(oldItem: PhotoAlbum, newItem: PhotoAlbum) = oldItem == newItem
        }
    }
}