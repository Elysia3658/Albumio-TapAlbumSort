package com.example.albumio.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.albumio.databinding.FragmentAlbumFolderBinding
import com.example.albumio.logic.viewModel.AlbumFolderViewModel
import com.example.albumio.ui.adapter.AlbumPagerAdapter
import com.example.albumio.ui.function.PaddingDecoration
import kotlinx.coroutines.launch


class AlbumFolderFragment : Fragment() {

    private var _binding : FragmentAlbumFolderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlbumFolderViewModel by viewModels()
    private val recyclerView : RecyclerView by lazy { binding.recyclerViewAlbumFolder }
    private lateinit var adapter : AlbumPagerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlbumFolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(requireContext(), SortingActivity::class.java).apply {

            putExtra(
                "albumId",
                991102496.toLong()
            )
            putExtra("albumName", "QQ")
            putExtra("coverUri", "content://media/external/images/media/1000231682")
        } //TODO:暂时使用qq作为启动页，记得改回去

        requireContext().startActivity(intent)

        adapter = AlbumPagerAdapter()
        recyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(PaddingDecoration(20))

        streamObservers()
        bottomObservers()
    }

    fun streamObservers() {
        // 在 Activity/Fragment 生命周期作用域内开启一个协程
        lifecycleScope.launch {
            val response = viewModel.pager
            response.collect { pagingData ->
                adapter.submitData(pagingData)}
        }
    }

    fun bottomObservers() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}