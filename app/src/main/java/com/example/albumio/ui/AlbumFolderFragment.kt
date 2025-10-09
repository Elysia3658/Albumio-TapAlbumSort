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
import kotlinx.coroutines.launch


class AlbumFolderFragment : Fragment() {

    private var _binding : FragmentAlbumFolderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlbumFolderViewModel by viewModels()
    private val recyclerView : RecyclerView by lazy { binding.recyclerView }


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

        val layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.layoutManager = layoutManager

        streamObservers()
        bottomObservers()
    }

    fun streamObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.count.collect { value ->
                    binding.textView.text = value.toString()
                }
            }
        }
    }

    fun bottomObservers() {
        binding.button.setOnClickListener {
            viewModel.incrementCount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}