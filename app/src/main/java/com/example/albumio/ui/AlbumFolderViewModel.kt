package com.example.albumio.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AlbumFolderViewModel : ViewModel() {
    private val _count = MutableStateFlow(0) // 初始值为 0
    val count: StateFlow<Int> = _count      // 对外暴露不可变 StateFlow

    fun incrementCount() {
        _count.value += 1
    }
}