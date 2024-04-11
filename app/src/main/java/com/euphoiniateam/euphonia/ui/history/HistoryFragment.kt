package com.euphoiniateam.euphonia.ui.history

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.euphoiniateam.euphonia.databinding.FragmentHistoryBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    lateinit var listAdapter: HistoryAdapter

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
            getMidFileNamesFromExternalStorage(),
            findNavController()
        )
        recyclerView.adapter = listAdapter

        /*val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Euphonia")
        dir.mkdirs()
        saveMidiToExternalStorage(requireContext(), R.raw.bumer, dir, "bumer.mid")*/
        // val midiResourceId = R.raw.angra_carolina_iv
        // val midiInputStream: InputStream = resources.openRawResource(midiResourceId)
        // saveMidiToInternalStorage(requireContext(), midiInputStream, "angra_carolina_iv.mid")

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

    private fun getMidFileNamesFromExternalStorage(): ArrayList<String> {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Euphonia"
        )
        dir.mkdirs()
        val midiFilesNames = ArrayList<String>()
        dir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension.equals("mid", ignoreCase = true)) {
                midiFilesNames.add(file.name)
            }
        }
        return midiFilesNames
    }

    // Функция для сохранения midi файлов в internal storage
    private fun saveMidiToExternalStorage(
        context: Context,
        resourceId: Int,
        targetDirectory: File,
        fileName: String
    ) {
        val inputStream: InputStream = context.resources.openRawResource(resourceId)
        val outputStream = FileOutputStream(File(targetDirectory, fileName))
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }
}
