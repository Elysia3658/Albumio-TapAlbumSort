package com.example.albumio.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.albumio.databinding.ActivitySortingBinding
import com.example.albumio.logic.data_class.Album
import com.example.albumio.logic.viewModel.SortingViewModel
import com.example.albumio.myClass.PhotoPagerAdapter
import com.example.albumio.ui.adapter.ImageMovesButtonsAdapter
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.launch

class SortingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySortingBinding
    private val viewPager by lazy { binding.viewPager }
    private val buttonsRecyclerView by lazy { binding.imageMovesButtons }
    private val viewModel: SortingViewModel by viewModels ()
    private val photoAdapter = PhotoPagerAdapter()
    private val buttonsAdapter = ImageMovesButtonsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySortingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intent = intent
        val albumId = intent.getLongExtra("albumId", -1)
        val albumName = intent.getStringExtra("albumName") ?: "Album"
        val coverUri = intent.getStringExtra("coverUri") ?: ""

        streamObservers(albumId)

        viewPager.adapter = photoAdapter

        val albumList = listOf(
            Album(
                id = 1L,
                name = "旅行",
                coverUri = "content://media/external/images/media/1001".toUri(),
                photoCount = 52
            ),
            Album(
                id = 2L,
                name = "家庭",
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
                name = "工作",
                coverUri = Uri.parse("content://media/external/images/media/1004"),
                photoCount = 10
            )
            ,
            Album(
                id = 5L,
                name = "宠物",
                coverUri = Uri.parse("content://media/external/images/media/1005"),
                photoCount = 23
            )
        )

        buttonsAdapter.submitList(albumList)

        buttonsRecyclerView.adapter = buttonsAdapter
        val flexboxLayoutManager =FlexboxLayoutManager(this).apply{
            flexDirection = FlexDirection.ROW          // 主轴横向
            flexWrap = FlexWrap.WRAP                   // 允许换行
            justifyContent = JustifyContent.SPACE_AROUND // 行内靠左
            alignItems = AlignItems.FLEX_START         // 每行顶部对齐
        }

        buttonsRecyclerView.layoutManager = flexboxLayoutManager

    }

    fun streamObservers(albumId : Long) {
        viewModel.getAlbumPhotos(albumId)
        // 在 Activity/Fragment 生命周期作用域内开启一个协程
        lifecycleScope.launch {
            val response = viewModel.pager
            response.collect { pagingData ->
                photoAdapter.submitData(pagingData)
            }
        }
    }

}