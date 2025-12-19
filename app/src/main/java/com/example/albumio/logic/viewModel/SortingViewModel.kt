package com.example.albumio.logic.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.albumio.logic.model.MediaStoreRepository
import com.example.albumio.myClass.UriListPagingSource
import kotlinx.coroutines.flow.Flow

class SortingViewModel(app: Application) : AndroidViewModel(app) {

    private val mediaStoreRepository = MediaStoreRepository(app)
    lateinit var pager: Flow<PagingData<Uri>>

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


}