package com.example.albumio.logic.model

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.albumio.logic.data.Album

// 定义一个 Repository 类，用于操作 MediaStore 图片数据
// 通过传入 Context 来获取 ContentResolver
class MediaStoreRepository(private val context: Context) {


    // ContentResolver 是 Android 用来访问内容提供者（ContentProvider）的接口
    // MediaStore 就是一个内容提供者，存储了系统的图片、视频、音频等
    private val contentResolver: ContentResolver = context.contentResolver

    // 数据类：表示单张图片的信息
    data class ImageItem(
        val id: Long,               // MediaStore 中图片的唯一 ID
        val displayName: String,    // 图片文件名
        val bucketName: String,     // 图片所在相册/文件夹名
        val relativePath: String,   // 图片在存储中的相对路径（Android 10+ 的分区存储）
        val contentUri: Uri,        // 图片的 Content URI，可以用来加载或分享
        val size: Long,             // 图片大小（字节）
        val dateAdded: Long         // 图片添加时间（UNIX 时间戳）
    )


    fun queryAlbumFolders(): List<Album>{
        val albumList = mutableListOf<Album>()

        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media._ID
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use { c ->
            val bucketIdColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val idColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            val albumMap = mutableMapOf<Long, Album>()

            while (c.moveToNext()) {
                val bucketId = c.getLong(bucketIdColumn)
                val bucketName = c.getString(bucketNameColumn)
                val imageId = c.getLong(idColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
                )

                val album = albumMap.getOrPut(bucketId) {
                    Album(
                        id = bucketId,
                        name = bucketName,
                        coverUri = contentUri,
                        photoCount = 0
                    )
                }

                albumMap[bucketId] = album.copy(photoCount = album.photoCount + 1)
            }

            albumList.addAll(albumMap.values)
        }

        return albumList
    }


    // 查询所有图片的方法
    fun queryAllImages(): List<ImageItem> {
        val imageList = mutableListOf<ImageItem>()  // 用于存储查询到的图片列表

        // 要查询的列名（Projection）
        val projection = arrayOf(
            MediaStore.Images.Media._ID,               // 图片 ID
            MediaStore.Images.Media.DISPLAY_NAME,      // 图片名称
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,// 相册名
            MediaStore.Images.Media.RELATIVE_PATH,     // 相对路径
            MediaStore.Images.Media.SIZE,              // 图片大小
            MediaStore.Images.Media.DATE_ADDED         // 添加时间
        )

        // 排序规则：按添加时间倒序
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        // 调用 contentResolver.query 查询 MediaStore
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 外部存储的图片 URI
            projection,      // 查询列
            null,            // 查询条件
            null,            // 查询条件参数
            sortOrder        // 排序
        )

        // 使用 cursor?.use { } 确保 cursor 最终关闭，避免内存泄漏
        cursor?.use { c ->
            // 获取每列在 cursor 中的索引
            val idColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val bucketColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val pathColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            val sizeColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            // 遍历每一行
            while (c.moveToNext()) {
                val id = c.getLong(idColumn)             // 获取图片 ID
                val name = c.getString(nameColumn)       // 获取图片名称
                val bucketName = c.getString(bucketColumn) ?: "Unknown" // 获取相册名，防空
                val relativePath = c.getString(pathColumn) ?: ""        // 获取相对路径
                val size = c.getLong(sizeColumn)         // 获取图片大小
                val dateAdded = c.getLong(dateColumn)    // 获取添加时间

                // 构造 Content URI，用于加载或分享图片
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                // 添加到列表
                imageList.add(ImageItem(id, name, bucketName, relativePath, contentUri, size, dateAdded))
            }
        }

        return imageList // 返回查询到的所有图片
    }



    // 条件查询：根据文件夹名称查询
    fun queryImagesByAlbum(albumId: Long): List<ImageItem> {
        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?" // 查询条件
        val selectionArgs = arrayOf(albumId.toString())                                // 条件参数

        return queryImagesWithSelection(selection, selectionArgs)             // 调用通用查询方法
    }

    // 条件查询：最近几天的照片
    fun queryRecentImages(days: Int = 7): List<ImageItem> {
        val minDate = System.currentTimeMillis() / 1000 - (days * 24 * 60 * 60) // 计算 UNIX 时间戳
        val selection = "${MediaStore.Images.Media.DATE_ADDED} > ?"             // 条件：添加时间大于 minDate
        val selectionArgs = arrayOf(minDate.toString())                         // 参数

        return queryImagesWithSelection(selection, selectionArgs)
    }

    // 内部通用查询方法，支持 selection 条件
    private fun queryImagesWithSelection(selection: String?, selectionArgs: Array<String>?): List<ImageItem> {
        val imageList = mutableListOf<ImageItem>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.RELATIVE_PATH,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_ADDED
        )

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )

        cursor?.use { c ->
            val idColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val bucketColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val pathColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            val sizeColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (c.moveToNext()) {
                val id = c.getLong(idColumn)
                val name = c.getString(nameColumn)
                val bucketName = c.getString(bucketColumn) ?: "Unknown"
                val relativePath = c.getString(pathColumn) ?: ""
                val size = c.getLong(sizeColumn)
                val dateAdded = c.getLong(dateColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                imageList.add(ImageItem(id, name, bucketName, relativePath, contentUri, size, dateAdded))
            }
        }

        return imageList
    }
}