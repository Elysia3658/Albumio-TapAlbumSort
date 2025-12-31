package com.example.albumio.logic.commandPattern

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

class PhotosPageChangedByUser(private val unRecordedCurrentPage: Int) : Command,
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