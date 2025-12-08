package com.example.albumio.logic.data_class

import android.net.Uri

data class Album(
    val id: Long,
    val name: String,
    val coverUri: Uri,
    val photoCount: Int
)