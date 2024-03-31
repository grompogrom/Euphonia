package com.euphoiniateam.euphonia.ui.home

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val getContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bundle = Bundle()
            bundle.putString("uri", uri.toString())
            val navController = findNavController()
            navController.navigate(R.id.action_navigation_home_to_creationFragment, bundle)
        }
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getContent.launch("audio/midi")
        } else {
            Toast.makeText(context, "permisson not granted", Toast.LENGTH_SHORT).show()
        }
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

        return root
    }

    private fun getFileReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission.launch(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
