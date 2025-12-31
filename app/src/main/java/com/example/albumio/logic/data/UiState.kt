package com.example.albumio.logic.data

interface UiState
data class PhotoUiState (
    val currentPage: Int = 0,
    val canGoNext: Boolean = true
) : UiState


data class ButtonUiState(
    val canUndo: Boolean = false
) : UiState