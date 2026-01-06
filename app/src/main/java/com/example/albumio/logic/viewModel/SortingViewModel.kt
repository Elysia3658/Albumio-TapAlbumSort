package com.example.albumio.logic.viewModel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.albumio.logic.commandPattern.Command
import com.example.albumio.logic.commandPattern.CommandManager
import com.example.albumio.logic.commandPattern.PhotosCopyCommand
import com.example.albumio.logic.commandPattern.PhotosDeleteCommand
import com.example.albumio.logic.commandPattern.PhotosMoveCommand
import com.example.albumio.logic.commandPattern.PhotosNextCommand
import com.example.albumio.logic.commandPattern.PhotosPageChangedByUserCommand
import com.example.albumio.logic.data.ButtonUiState
import com.example.albumio.logic.data.PhotoAlbum
import com.example.albumio.logic.data.PhotoUiState
import com.example.albumio.logic.model.MediaStoreRepository
import com.example.albumio.myClass.UriListPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SortingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaStoreRepository: MediaStoreRepository
) : ViewModel() {


    private val mediaStoreResolver: ContentResolver by lazy { context.contentResolver }
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

            is PhotosPageChangedByUserCommand -> {
                val newPhotosState = command.uiRecord(_photoState.value)
                _photoState.value = newPhotosState
                commandManager.addCommand(command)
            }

            is PhotosCopyCommand -> TODO()
            is PhotosDeleteCommand -> TODO()
            is PhotosMoveCommand -> {
                // First, execute logic
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

            is PhotosPageChangedByUserCommand -> {
                val newPhotosState = command.uiUndoRecord()
                _photoState.value = newPhotosState
            }

            is PhotosCopyCommand -> {
                TODO()
            }

            is PhotosDeleteCommand -> {
                TODO()
            }

            is PhotosMoveCommand -> {
                TODO()
            }
        }
    }


    fun textAlbumList(): List<PhotoAlbum> {
        val photoAlbumLists = listOf(
            PhotoAlbum(
                albumId = 1L,
                albumPath = "path/to/album1",
                albumName = "组图表情包",
                coverPhotoUri = "content://media/external/images/media/1001".toUri(),
                photoCount = 52
            ),
            PhotoAlbum(
                albumId = 2L,
                albumPath = "path/to/album2",
                albumName = "情绪表情包",
                coverPhotoUri = Uri.parse("content://media/external/images/media/1002"),
                photoCount = 34
            ),
            PhotoAlbum(
                albumId = 3L,
                albumPath = "path/to/album3",
                albumName = "朋友",
                coverPhotoUri = Uri.parse("content://media/external/images/media/1003"),
                photoCount = 87
            ),
            PhotoAlbum(
                albumId = 4L,
                albumPath = "path/to/album4",
                albumName = "同样超级长的文本大测试看看效果如何",
                coverPhotoUri = Uri.parse("content://media/external/images/media/1004"),
                photoCount = 10
            ),
            PhotoAlbum(
                albumId = 5L,
                albumPath = "path/to/album5",
                albumName = "超级长的文本大测试看看效果如何",
                coverPhotoUri = Uri.parse("content://media/external/images/media/1005"),
                photoCount = 23
            ),
            PhotoAlbum(
                albumId = 6L,
                albumPath = "path/to/album6",
                albumName = "测试",
                coverPhotoUri = Uri.parse("content://media/external/images/media/1006"),
                photoCount = 45
            ),
            PhotoAlbum(
                albumId = 7L,
                albumPath = "path/to/album7",
                albumName = "旅行",
                coverPhotoUri = Uri.parse("content://media/external/images/media/1007"),
                photoCount = 78
            )
        )

        return photoAlbumLists
    }

}