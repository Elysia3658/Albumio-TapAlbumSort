package com.example.albumio.logic.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.albumio.logic.data.PhotoAlbum

class AlbumFolderPagingSource(
    private val photoAlbumList : List<PhotoAlbum>
) : PagingSource<Int, PhotoAlbum>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoAlbum> {
        // 获取当前页码，如果是第一次加载则为null，这里设为0表示从第一页开始
        val page = params.key ?: 0
        // 获取请求的页面大小
        val pageSize = params.loadSize

        // 计算当前页在完整列表中的起始索引
        val fromIndex = page * pageSize
        // 计算结束索引，确保不超过列表边界
        val toIndex = minOf(fromIndex + pageSize, photoAlbumList.size)

        // 从完整列表中截取当前页的数据子集
        val sublist = if (fromIndex < photoAlbumList.size) {
            photoAlbumList.subList(fromIndex, toIndex)  // 安全截取子列表
        } else {
            emptyList()  // 如果起始索引超出范围，返回空列表
        }

        // 返回分页结果
        return LoadResult.Page(
            data = sublist,  // 当前页的数据
            prevKey = if (page == 0) null else page - 1,  // 上一页键：如果是第一页则为null
            nextKey = if (toIndex < photoAlbumList.size) page + 1 else null  // 下一页键：如果还有数据则为下一页编号
        )
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoAlbum>): Int? {
        // 获取 RecyclerView 当前可视区域的锚点位置（通常是屏幕中间可见的 item）
        return state.anchorPosition?.let { anchorPosition ->

            // 找到与锚点位置最近的 Page（分页块）
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)


        }
    }
}