package com.example.albumio.logic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.albumio.myClass.MediaStoreRepository

class AlbumFolderViewModel(app: Application) : AndroidViewModel(app){
    private val mediaStoreRepository = MediaStoreRepository(app)

    val albumFolders = mediaStoreRepository.queryAlbumFolders()
    val pager = Pager(  // 创建Pager实例，用于生成分页数据流
        config = PagingConfig(  // 配置分页参数
            pageSize = 20,             // 每页加载20个项目
            enablePlaceholders = false // 禁用占位符（当数据未加载时不显示占位UI）
        ),
        pagingSourceFactory = { AlbumFolderPagingSource(albumFolders) }
        // 创建PagingSource的工厂函数，这里使用MediaStorePagingSource来处理媒体库数据
    ).flow  // 将Pager转换为Flow，便于在协程中消费，这里是冷流进行发送
        .cachedIn(viewModelScope)  // 在ViewModel作用域内缓存数据，避免配置变更时重新加载(activity内就用lifecycleScope)





}