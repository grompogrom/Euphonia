package com.euphoiniateam.euphonia.ui.record

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.euphoiniateam.euphonia.databinding.FragmentHomeListenBinding
import com.euphoiniateam.euphonia.tools.MicrophoneFunctions
import com.euphoiniateam.euphonia.ui.home.HomeViewModel

class HomeListenFragment : Fragment() {

    private var _binding: FragmentHomeListenBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeListenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        context?.applicationContext?.let { it1 -> MicrophoneFunctions.startRecording(it1) }
        val btnOpenHome: ImageButton = binding.btnToEndMicro
        btnOpenHome.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                binding.v1After to "v1",
                binding.v2After to "v2",
                binding.btnToEndMicro to "micro"
            )
            context?.let { it2 -> MicrophoneFunctions.stopRecording(it2) }
            val action =
                HomeListenFragmentDirections.actionHomeListenFragmentToCreationFragment()
            val navController = findNavController()
            navController.navigate(action, navigatorExtras = extras)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
