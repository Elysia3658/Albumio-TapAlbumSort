package com.example.albumio.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.albumio.R
import com.example.albumio.databinding.ActivitySortingBinding
import com.example.albumio.logic.commandPattern.PhotosNextCommand
import com.example.albumio.logic.commandPattern.PhotosPageChangedByUserCommand
import com.example.albumio.logic.data.PhotoAlbum
import com.example.albumio.logic.viewModel.SortingViewModel
import com.example.albumio.myClass.PhotoPagerAdapter
import com.example.albumio.ui.adapter.ImageMovesButtonsAdapter
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SortingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySortingBinding
    private val viewPager by lazy { binding.viewPager }
    private val buttonsRecyclerView by lazy { binding.imageMovesButtons }
    private val nextButton by lazy { binding.nextPagerButton }
    private val undoButton by lazy { binding.undoButton }
    private val viewModel: SortingViewModel by viewModels()
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


        viewPager.adapter = photoAdapter

        val photoAlbumList: List<PhotoAlbum> = viewModel.textAlbumList()
        buttonsAdapter.submitList(photoAlbumList)

        buttonsRecyclerView.adapter = buttonsAdapter
        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
            flexDirection = FlexDirection.ROW          // 主轴横向
            flexWrap = FlexWrap.WRAP                 // 允许换行
            justifyContent = JustifyContent.SPACE_AROUND // 行内靠左
            alignItems = AlignItems.FLEX_START         // 每行顶部对齐
        }
        buttonsRecyclerView.layoutManager = flexboxLayoutManager

        viewPagerOverride()
        streamObserversForUi(albumId)
        buttonOnClick()

    }

    fun streamObserversForUi(albumId: Long) {
        viewModel.getAlbumPhotos(albumId)
        // 在 Activity/Fragment 生命周期作用域内开启一个协程
        lifecycleScope.launch {
            val response = viewModel.pager
            response.collect { pagingData ->
                photoAdapter.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            viewModel.photoState.collect { photoUiState ->
                if (viewPager.currentItem != photoUiState.currentPage) {
                    viewPager.currentItem = photoUiState.currentPage
                }
            }
        }

        lifecycleScope.launch {
            viewModel.buttonsState.collect { buttonsState ->
                when(buttonsState.canUndo) {
                    true -> {
                        undoButton.isEnabled = true
                        undoButton.imageTintList = AppCompatResources.getColorStateList(this@SortingActivity, R.color.operate_word_color)
                    }
                    false -> {
                        undoButton.isEnabled = false
                        undoButton.imageTintList = AppCompatResources.getColorStateList(this@SortingActivity, R.color.disable_button_color)
                    }
                }
            }
        }
    }


    fun buttonOnClick() {
        nextButton.setOnClickListener {
            viewModel.sendCommand(
                PhotosNextCommand()
            )
        }

        undoButton.setOnClickListener {
            viewModel.undoCommand()
        }
    }

    fun viewPagerOverride() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val currentState = viewModel.photoState.value
                if (position != currentState.currentPage) {
                    viewModel.sendCommand(PhotosPageChangedByUserCommand(position))
                }
            }
        })
    }

}