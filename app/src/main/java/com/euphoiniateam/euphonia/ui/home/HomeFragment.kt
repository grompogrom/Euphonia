package com.euphoiniateam.euphonia.ui.home

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.euphoiniateam.euphonia.databinding.FragmentHomeBinding
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val animation = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation

        val btnOpenPiano: ImageButton = binding.btnToPiano
        btnOpenPiano.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToPianoFragment()
            val navController = findNavController()
            navController.navigate(action)
        }

        val btnStartMicro: ImageButton = binding.btnToMicro
        btnStartMicro.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.v1Before to "v1", binding.v2Before to "v2", binding.btnToMicro to "micro")
            val action = HomeFragmentDirections.actionNavigationHomeToHomeListenFragment()
            val navController = findNavController()
            navController.navigate(
                action,
                navigatorExtras = extras
            )
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}