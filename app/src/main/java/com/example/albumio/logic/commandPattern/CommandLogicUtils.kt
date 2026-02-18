package com.example.albumio.logic.commandPattern

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore

fun insertMediaStoreValues(
    fileName: String,
    mimeType: String,
    targetAlbumPath: String
): ContentValues {
    return ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        put(MediaStore.Images.Media.RELATIVE_PATH, targetAlbumPath)
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }
}

fun copyPhotoInMediaStore(
    resolver: ContentResolver,
    srcUri: Uri,
    targetInsertValues: ContentValues
): Uri {
    val targetUri =
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, targetInsertValues)
            ?: throw Exception("目标Uri创建失败ERROR")

    try {
        resolver.openInputStream(srcUri).use { inputStream ->
            resolver.openOutputStream(targetUri).use { outputStream ->
                if (inputStream == null || outputStream == null) {
                    throw Exception("复制流打开失败ERROR")
                }
                inputStream.copyTo(outputStream)
            }
        }

        // 更新 IS_PENDING 状态，表示文件已准备好被访问
        val updateValues = ContentValues().apply {
            put(MediaStore.Images.Media.IS_PENDING, 0)
        }
        resolver.update(targetUri, updateValues, null, null)
    } catch (e: Exception) {
        // 如果复制过程中出错，删除目标 Uri 以避免残留无效数据
        resolver.delete(targetUri, null, null)
        throw e
    }

    return targetUri
}

fun movePhotoInMediaStore(
    resolver: ContentResolver,
    srcUri: Uri,
    targetInsertValues: ContentValues
): Uri {
    val targetUri = copyPhotoInMediaStore(resolver, srcUri, targetInsertValues)
    resolver.delete(srcUri, null, null)
    return targetUri
}

