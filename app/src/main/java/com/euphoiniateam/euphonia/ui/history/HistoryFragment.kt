package com.euphoiniateam.euphonia.ui.history

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentHistoryBinding
import com.euphoiniateam.euphonia.tools.getMidFileNamesFromPiano
import com.euphoiniateam.euphonia.tools.getMidFileNamesFromResults
import com.euphoiniateam.euphonia.ui.MidiFile
import com.euphoiniateam.euphonia.ui.MidiPlayer
import com.euphoiniateam.euphonia.ui.creation.CreationFragment

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    lateinit var listAdapter: HistoryAdapter

    private var currentTab = CurrentTab.Piano

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
            ::navigateToCreation,
            currentTab,
            MidiPlayer()
        )
        recyclerView.adapter = listAdapter
        val searchTextId = searchView.context.resources.getIdentifier(
            "android:id/search_src_text",
            null,
            null
        )
        val searchText = searchView.findViewById<TextView>(searchTextId)
        searchText.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.md_theme_light_outlineVariant)
        )
        searchText.setHintTextColor(
            ContextCompat.getColor(requireContext(), R.color.md_theme_light_outlineVariant)
        )
        val searchUnderlineId = searchView.context.resources.getIdentifier(
            "android:id/search_plate",
            null,
            null
        )
        val searchUnderline = searchView.findViewById<View>(searchUnderlineId)
        searchUnderline.background = null
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
            if (currentTab != CurrentTab.Piano) {
                currentTab = CurrentTab.Piano
                listAdapter.setData(getMidFileNamesFromPiano(), currentTab)
                binding.btnShowPlayed.setBackgroundResource(R.drawable.button_bottom_border)
                binding.btnShowGenerated.background = null
            }
        }

        binding.btnShowGenerated.setOnClickListener {
            if (currentTab != CurrentTab.Result) {
                currentTab = CurrentTab.Result
                listAdapter.setData(getMidFileNamesFromResults(), currentTab)
                binding.btnShowGenerated.setBackgroundResource(R.drawable.button_bottom_border)
                binding.btnShowPlayed.background = null
            }
        }
        return rootView
    }

    private fun navigateToCreation(uri: Uri) {
        val bundle = Bundle()
        bundle.putSerializable(CreationFragment.URI_KEY, MidiFile(uri.toString()))
        findNavController().navigate(
            R.id.action_navigation_dashboard_to_creationFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class CurrentTab {
    Piano,
    Result
}
