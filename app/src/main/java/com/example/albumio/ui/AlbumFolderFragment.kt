package com.example.albumio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.albumio.databinding.FragmentAlbumFolderBinding
import com.example.albumio.logic.AlbumFolderViewModel
import com.example.albumio.ui.adapter.AlbumPagerAdapter
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

        adapter = AlbumPagerAdapter()
        recyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.layoutManager = layoutManager

        streamObservers()
        bottomObservers()
    }

    fun streamObservers() {
        // 在 Activity/Fragment 生命周期作用域内开启一个协程
        lifecycleScope.launch {
            // 收集 Pager 产生的 Flow<PagingData<T>>
            // collectLatest 表示只处理最新的数据流
            // 如果在处理上一次分页数据时又来了新的，就会取消旧的，保证只显示最新结果
            val response = viewModel.a()
            response.collect { pager ->
                adapter.submitData(pager}
        }
    }

    fun bottomObservers() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}