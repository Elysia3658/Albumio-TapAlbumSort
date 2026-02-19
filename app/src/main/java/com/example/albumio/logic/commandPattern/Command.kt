package com.example.albumio.logic.commandPattern

import android.content.ContentResolver
import android.net.Uri
import com.example.albumio.logic.data.ImageItem
import com.example.albumio.logic.data.PhotoUiState

sealed interface Command

class PhotosNextCommand() : Command, UiMutator<PhotoUiState> {
    private lateinit var snapshot: PhotoUiState
    override fun uiExecute(oldState: PhotoUiState): PhotoUiState {
        snapshot = oldState
        val newState = oldState.copy(
            currentPage = oldState.currentPage + 1
        )
        return newState
    }

    override fun uiUndo(): PhotoUiState {
        return snapshot
    }
}

class PhotosPageChangedByUserCommand(private val unRecordedCurrentPage: Int) : Command,
    UiRecordByUser<PhotoUiState> {
    private lateinit var snapshot: PhotoUiState
    override fun uiRecord(oldState: PhotoUiState): PhotoUiState {
        snapshot = oldState
        val currentState = oldState.copy(
            currentPage = unRecordedCurrentPage
        )
        return currentState
    }

    override fun uiUndoRecord(): PhotoUiState {
        return snapshot
    }
}

class PhotosMoveCommand(
    private val srcPhotoInfo: ImageItem,
    private val targetAlbumPath: String,
    private val mutator: UiMutator<PhotoUiState>
) : Command, UndoLogicRunner, UiMutator<PhotoUiState> by mutator {
    private lateinit var uriSnapshot: Uri
    private val previousAlbumPath = srcPhotoInfo.relativePath
    override fun logicExecute(resolver: ContentResolver) {
        val srcUri = srcPhotoInfo.contentUri
        val mimeType = srcPhotoInfo.mimeType
        val fileName = srcPhotoInfo.fileName

        val targetInsertValues = insertMediaStoreValues(fileName, mimeType, targetAlbumPath)
        uriSnapshot = movePhotoInMediaStore(resolver, srcUri, targetInsertValues)
    }
    // TODO:这里会直接删除，等排列自动做好后记得改bug
    // TODO: 对于这里的操作，需要进行同步相册相关,防止移动后还在的情况

    override fun logicUndo(resolver: ContentResolver) {
        val newUri = uriSnapshot
        val mimeType = srcPhotoInfo.mimeType
        val fileName = srcPhotoInfo.fileName

        val targetInsertValues = insertMediaStoreValues(fileName, mimeType, previousAlbumPath)
        movePhotoInMediaStore(resolver, newUri, targetInsertValues)
    }
}

class PhotosCopyCommand(
    private val srcPhotoInfo: ImageItem,
    private val targetAlbumPath: String
) : Command, UndoLogicRunner {
    private lateinit var uriSnapshot: Uri

    override fun logicExecute(resolver: ContentResolver) {
        val srcUri = srcPhotoInfo.contentUri
        val mimeType = srcPhotoInfo.mimeType
        val fileName = srcPhotoInfo.fileName

        val targetInsertValues = insertMediaStoreValues(fileName, mimeType, targetAlbumPath)
        copyPhotoInMediaStore(resolver, srcUri, targetInsertValues)
    }

    override fun logicUndo(resolver: ContentResolver) {
        val newUri = uriSnapshot
        resolver.delete(newUri, null, null)
    }
}

class PhotosDeleteCommand(
    private val srcPhotoInfo: ImageItem
) : Command, BaseLogicRunner {
    override fun logicExecute(resolver: ContentResolver) {
        val srcUri = srcPhotoInfo.contentUri
        resolver.delete(srcUri, null, null)
    }

}