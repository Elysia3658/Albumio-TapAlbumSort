package com.example.albumio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.albumio.databinding.ActivityMainBinding
import com.example.albumio.myClass.MediaStoreRepository
import com.example.albumio.myClass.PhotoPagerAdapter
import com.example.albumio.myClass.UriListPagingSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var viewPager2 = lazy { binding.viewPager }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = "package:$packageName".toUri()
                intent.data = uri
                startActivity(intent)
            }
        }

        val mediaStoreRepository = MediaStoreRepository(this)
        val albumImages  = mediaStoreRepository.queryAllImages()
        val uris :List<Uri> = albumImages.map { it.contentUri }

        val pager = Pager(  // 创建Pager实例，用于生成分页数据流
            config = PagingConfig(  // 配置分页参数
                pageSize = 1,             // 每页加载20个项目
                enablePlaceholders = false // 禁用占位符（当数据未加载时不显示占位UI）
            ),
            pagingSourceFactory = { UriListPagingSource(uris) }
            // 创建PagingSource的工厂函数，这里使用MediaStorePagingSource来处理媒体库数据
        ).flow  // 将Pager转换为Flow，便于在协程中消费
            .cachedIn(lifecycleScope)  // 在ViewModel作用域内缓存数据，避免配置变更时重新加载


        val adapter = PhotoPagerAdapter()
        viewPager2.value.adapter = adapter

        lifecycleScope.launch {
            pager.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

    }
}