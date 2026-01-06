package com.example.albumio.logic.commandPattern

import android.content.ContentResolver
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
) : Command, LogicRunner {
    override fun logicExecute(mediaStoreResolver: ContentResolver) {
        // Logic to move photo from fromPosition to toPosition
    }

    override fun logicUndo() {
        // Logic to undo the move operation
    }
}

class PhotosCopyCommand(
    private val position: Int
) : Command, LogicRunner {
    override fun logicExecute(mediaStoreResolver: ContentResolver) {
        // Logic to copy photo at position
    }

    override fun logicUndo() {
        // Logic to undo the copy operation
    }
}

class PhotosDeleteCommand(
    private val srcPhotoInfo: ImageItem
) : Command, LogicRunner {
    override fun logicExecute(mediaStoreResolver: ContentResolver) {
        // Logic to delete photo at position
    }

    override fun logicUndo() {
        // Logic to undo the delete operation
    }
}