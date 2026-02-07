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
    private val targetAlbumPath: String
) : Command, UndoLogicRunner {
    private lateinit var uriSnapshot: Uri
    private val previousAlbumPath = srcPhotoInfo.relativePath
    override fun logicExecute(resolver: ContentResolver) {
        val srcUri = srcPhotoInfo.contentUri
        val mimeType = srcPhotoInfo.mimeType
        val fileName = srcPhotoInfo.fileName

        val targetInsertValues = insertMediaStoreValues(fileName, mimeType, targetAlbumPath)
        uriSnapshot = movePhotoInMediaStore(resolver, srcUri, targetInsertValues)
    }

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