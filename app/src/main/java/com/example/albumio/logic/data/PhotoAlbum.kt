package com.example.albumio.logic.data

import android.net.Uri

data class PhotoAlbum(
    val albumId: Long,
    val albumPath: String,
    val albumName: String,
    val coverPhotoUri: Uri,
    val photoCount: Int
)