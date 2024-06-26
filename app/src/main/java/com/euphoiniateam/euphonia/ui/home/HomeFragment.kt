package com.euphoiniateam.euphonia.ui.home

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentHomeBinding
import com.euphoiniateam.euphonia.ui.MidiFile
import com.euphoiniateam.euphonia.ui.creation.CreationFragment
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val fileExtensions: List<String> = listOf("rtx", "mid", "midi", "smf")

    private val getContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val extension = getMimeType(context, uri)
        if (fileExtensions.contains(extension)) {

            val bundle = Bundle()
            val userMidiFile = uri?.let { MidiFile(it.toString()) }
            bundle.putSerializable(CreationFragment.URI_KEY, userMidiFile)
            val navController = findNavController()
            navController.navigate(R.id.action_navigation_home_to_creationFragment, bundle)
        }
    }

    private val requestFilePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getContent.launch("audio/midi")
        } else {
            Toast.makeText(context, "permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestMicroPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navigateToMicro()
        } else {
            Toast.makeText(context, "permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMimeType(context: Context?, uri: Uri?): String? {
        val extension: String? = if (uri?.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context?.contentResolver!!.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(
                Uri.fromFile(uri?.path?.let { File(it) }).toString()
            )
        }
        return extension
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation

        val btnOpenPiano: ImageButton = binding.btnToPiano
        btnOpenPiano.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToPianoFragment()
            val navController = findNavController()
            navController.navigate(action)
        }

        binding.btnToFile.setOnClickListener {
            getFileReadPermission()
        }

        val btnStartMicro: ImageButton = binding.btnToMicro
        btnStartMicro.setOnClickListener {
            requestMicroPermission.launch(Manifest.permission.RECORD_AUDIO)
        }

        return root
    }

    private fun navigateToMicro() {
        val extras = FragmentNavigatorExtras(
            binding.v1Before to "v1",
            binding.v2Before to "v2",
            binding.btnToMicro to "micro"
        )
        val action = HomeFragmentDirections.actionNavigationHomeToHomeListenFragment()
        val navController = findNavController()
        navController.navigate(
            action,
            navigatorExtras = extras
        )
    }

    private fun getFileReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestFilePermission.launch(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            requestFilePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
