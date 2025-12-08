package com.example.albumio.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.albumio.databinding.ActivitySortingBinding
import com.example.albumio.logic.viewModel.SortingViewModel
import com.example.albumio.myClass.PhotoPagerAdapter
import kotlinx.coroutines.launch

class SortingActivity : AppCompatActivity() {

    private val adapter = PhotoPagerAdapter()
    private lateinit var binding: ActivitySortingBinding
    private val viewPager by lazy { binding.viewPager }
    private val viewModel: SortingViewModel by viewModels ()

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

        viewPager.adapter = adapter
    }

    fun streamObservers(albumId : Long) {
        viewModel.getAlbumPhotos(albumId)
        // 在 Activity/Fragment 生命周期作用域内开启一个协程
        lifecycleScope.launch {
            val response = viewModel.pager
            response.collect { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
}