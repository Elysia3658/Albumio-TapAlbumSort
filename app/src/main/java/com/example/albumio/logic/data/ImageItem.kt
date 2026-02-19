package com.example.albumio.logic.data

import android.net.Uri

data class ImageItem(
    val id: Long,               // MediaStore 中图片的唯一 ID
    val fileName: String,    // 图片文件名
    val mimeType: String,      // 图片 MIME 类型（如 image/jpeg）
    val bucketName: String,     // 图片所在相册/文件夹名
    val relativePath: String,   // 图片在存储中的相对路径（Android 10+ 的分区存储）
    val contentUri: Uri,        // 图片的 Content URI，可以用来加载或分享
    val size: Long,             // 图片大小（字节）
    val photoAddTime: Long         // 图片添加时间（UNIX 时间戳）
)