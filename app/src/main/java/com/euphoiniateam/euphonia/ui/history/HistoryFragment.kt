package com.euphoiniateam.euphonia.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentHistoryBinding
import com.euphoiniateam.euphonia.tools.getMidFileNamesFromPiano
import com.euphoiniateam.euphonia.tools.getMidFileNamesFromResults

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    lateinit var listAdapter: HistoryAdapter

    private var pianoData = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val rootView = binding.root
        recyclerView = binding.searchResults
        searchView = binding.idSV
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        listAdapter = HistoryAdapter(
            requireContext(),
            getMidFileNamesFromPiano(),
//            arrayListOf("First", "Second", "Third", "Песенка"),
            findNavController()
        )
        recyclerView.adapter = listAdapter
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    listAdapter.filter(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    listAdapter.filter(newText)
                    return false
                }
            }
        )
        binding.btnShowPlayed.setBackgroundResource(R.drawable.button_bottom_border)
        binding.btnShowGenerated.background = null
        binding.btnShowPlayed.setOnClickListener {
            if (!pianoData) {
                pianoData = true
                listAdapter.setData(getMidFileNamesFromPiano())
                binding.btnShowPlayed.setBackgroundResource(R.drawable.button_bottom_border)
                binding.btnShowGenerated.background = null
            }
        }

        binding.btnShowGenerated.setOnClickListener {
            if (pianoData) {
                pianoData = false
                listAdapter.setData(getMidFileNamesFromResults())
                binding.btnShowGenerated.setBackgroundResource(R.drawable.button_bottom_border)
                binding.btnShowPlayed.background = null
            }
        }
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
