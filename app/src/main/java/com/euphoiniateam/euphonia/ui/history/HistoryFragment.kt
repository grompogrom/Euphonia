package com.euphoiniateam.euphonia.ui.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.euphoiniateam.euphonia.databinding.FragmentHistoryBinding
import com.google.gson.Gson

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    lateinit var listAdapter: HistoryAdapter
    var dataList = arrayListOf<String>()

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
        listAdapter = HistoryAdapter(requireContext(), dataList)
        recyclerView.adapter = listAdapter

        val sharedPreferences = requireContext().getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()

        if (sharedPreferences.contains(KEY_ARRAY)) {
            val savedArray = getArrayFromSharedPreferences(requireContext())
            for (i in savedArray.indices) {
                dataList.add(savedArray[i])
            }
        } else {
            val gson = Gson()
            dataList.add("MyPlay")
            dataList.add("CoolSound")
            dataList.add("MyPlay1")
            dataList.add("Krutoten'")
            dataList.add("MyPlay2")
            dataList.add("MyPlay3")
            dataList.add("MyPlay4")
            dataList.add("Лунная соната Людвиг ван Бетховен.")
            val json = gson.toJson(dataList)
            editor.putString(KEY_ARRAY, json)
            editor.apply()
        }

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

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getArrayFromSharedPreferences(context: Context): Array<String> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return if (sharedPreferences.contains(KEY_ARRAY)) {
            val gson = Gson()
            val json = sharedPreferences.getString(KEY_ARRAY, null)
            gson.fromJson(json, Array<String>::class.java) ?: emptyArray()
        } else {
            emptyArray()
        }
    }

    companion object {
        private const val PREF_NAME = "Music"
        private const val KEY_ARRAY = "MusicVault"
    }
}

