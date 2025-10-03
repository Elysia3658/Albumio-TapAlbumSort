package com.example.albumio.myClass

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState

class UriListPagingSource(
    private val allUris: List<Uri>
) : PagingSource<Int, Uri>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Uri> {
        val page = params.key ?: 0
        val pageSize = params.loadSize
        val fromIndex = page * pageSize
        val toIndex = kotlin.math.min(fromIndex + pageSize, allUris.size)

        return if (fromIndex < allUris.size) {
            val pageData = allUris.subList(fromIndex, toIndex)
            LoadResult.Page(
                data = pageData,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (toIndex < allUris.size) page + 1 else null
            )
        } else {
            LoadResult.Page(
                data = emptyList(),
                prevKey = if (page == 0) null else page - 1,
                nextKey = null
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Uri>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}