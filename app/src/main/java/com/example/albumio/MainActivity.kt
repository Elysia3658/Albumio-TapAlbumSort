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
import com.example.albumio.databinding.ActivityMainBinding
import com.example.albumio.myClass.MediaStoreRepository
import com.example.albumio.myClass.PhotoPagerAdapter

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
        val albumImages  = mediaStoreRepository.queryAllImages().filterIndexed{ index, _ -> index < 50 }
        val uris :List<Uri> = albumImages.map { it.contentUri }


        val adapter = PhotoPagerAdapter(uris)
        viewPager2.value.adapter = adapter

    }
}