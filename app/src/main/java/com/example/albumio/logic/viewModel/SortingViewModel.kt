package com.example.albumio.logic.viewModel

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.albumio.logic.commandPattern.Command
import com.example.albumio.logic.commandPattern.CommandManager
import com.example.albumio.logic.commandPattern.PhotosNextCommand
import com.example.albumio.logic.commandPattern.PhotosPageChangedByUser
import com.example.albumio.logic.data.ButtonUiState
import com.example.albumio.logic.data.PhotoUiState
import com.example.albumio.logic.data_class.Album
import com.example.albumio.logic.model.MediaStoreRepository
import com.example.albumio.myClass.UriListPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SortingViewModel(app: Application) : AndroidViewModel(app) {

    private val mediaStoreRepository = MediaStoreRepository(app)
    private val commandManager by lazy { CommandManager() }
    lateinit var pager: Flow<PagingData<Uri>>
    private val _photoState = MutableStateFlow(PhotoUiState())
    val photoState: StateFlow<PhotoUiState> = _photoState
    private val _buttonsState = MutableStateFlow(ButtonUiState())
    val buttonsState: StateFlow<ButtonUiState> = _buttonsState

    init {
        observeOtherState()
    }

    private fun observeOtherState() {
        viewModelScope.launch {
            commandManager.undoAvailable.collect { canUndo ->
                val newButtonState = _buttonsState.value.copy(
                    canUndo = canUndo
                )
                _buttonsState.value = newButtonState
            }
        }
    }

    fun getAlbumPhotos(albumId: Long) {
        val originalPhotos = mediaStoreRepository.queryImagesByAlbum(albumId)
        val photos = originalPhotos.map { it.contentUri }
        pager = Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UriListPagingSource(photos)//TODO：这里需要改进为从数据库层面的分页
            }
        ).flow.cachedIn(viewModelScope)
    }


    fun sendCommand(command: Command) {
        when (command) {
            is PhotosNextCommand -> {
                val newPhotosState = command.uiExecute(_photoState.value)
                _photoState.value = newPhotosState
                commandManager.addCommand(command)
            }

            is PhotosPageChangedByUser -> {
                val newPhotosState = command.uiRecord(_photoState.value)
                _photoState.value = newPhotosState
                commandManager.addCommand(command)
            }
        }
    }


    fun undoCommand() {
        val command = commandManager.undoLastCommand()
        when (command) {
            is PhotosNextCommand -> {
                val newPhotosState = command.uiUndo()
                _photoState.value = newPhotosState
            }

            is PhotosPageChangedByUser -> {
                val newPhotosState = command.uiUndoRecord()
                _photoState.value = newPhotosState
            }
        }
    }


    fun textAlbumList(): List<Album> {
        val albumList = listOf(
            Album(
                id = 1L,
                name = "组图表情包",
                coverUri = "content://media/external/images/media/1001".toUri(),
                photoCount = 52
            ),
            Album(
                id = 2L,
                name = "情绪表情包",
                coverUri = Uri.parse("content://media/external/images/media/1002"),
                photoCount = 34
            ),
            Album(
                id = 3L,
                name = "朋友",
                coverUri = Uri.parse("content://media/external/images/media/1003"),
                photoCount = 87
            ),
            Album(
                id = 4L,
                name = "同样超级长的文本大测试看看效果如何",
                coverUri = Uri.parse("content://media/external/images/media/1004"),
                photoCount = 10
            ),
            Album(
                id = 5L,
                name = "超级长的文本大测试看看效果如何",
                coverUri = Uri.parse("content://media/external/images/media/1005"),
                photoCount = 23
            ),
            Album(
                id = 6L,
                name = "测试",
                coverUri = Uri.parse("content://media/external/images/media/1006"),
                photoCount = 45
            ),
            Album(
                id = 7L,
                name = "旅行",
                coverUri = Uri.parse("content://media/external/images/media/1007"),
                photoCount = 78
            )
        )

        return albumList
    }

}