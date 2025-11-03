package com.example.albumio.logic

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.albumio.logic.data.Album

class AlbumFolderPagingSource(
    private val albumList : List<Album>
) : PagingSource<Int, Uri>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Uri> {
        TODO("待完成")
    }

    override fun getRefreshKey(state: PagingState<Int, Uri>): Int? {
        TODO("提供返回值")
    }
}

