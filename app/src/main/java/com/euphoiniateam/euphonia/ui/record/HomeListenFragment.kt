package com.euphoiniateam.euphonia.ui.record

import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentHomeListenBinding
import com.euphoiniateam.euphonia.ui.MidiFile
import com.euphoiniateam.euphonia.ui.creation.CreationFragment
import kotlinx.coroutines.launch

class HomeListenFragment : Fragment() {

    private var _binding: FragmentHomeListenBinding? = null
    private var recordViewModel: RecordViewModel? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recordViewModel = ViewModelProvider(
            this,
            RecordViewModel.provideFactory(requireContext())
        ).get(RecordViewModel::class.java)

        _binding = FragmentHomeListenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        recordViewModel?.startRecording(requireContext().applicationContext)

        val btnOpenHome: ImageButton = binding.btnToEndMicro
        btnOpenHome.setOnClickListener {
            recordViewModel?.stopRecording()
        }
        observeScreenState()
        return root
    }

    private fun observeScreenState() {
        lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    recordViewModel?.screenState?.collect { state ->
                        when (state) {
                            is RecordScreenState.ERROR -> onError(state.description)
                            RecordScreenState.PROCESS -> {
                                showProcessing()
                            }

                            RecordScreenState.RECORDING -> {}
                            is RecordScreenState.SUCCESS -> {
                                onSuccess(state.uri)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onError(description: String) {
        Toast.makeText(requireContext(), "Error: $description", Toast.LENGTH_LONG).show()
        findNavController().navigateUp()
    }

    fun onSuccess(midi: Uri) {
        val navController = findNavController()
        val bundle = Bundle()
        bundle.putSerializable(CreationFragment.URI_KEY, MidiFile(midi.toString()))
        navController.navigate(R.id.action_homeListenFragment_to_creationFragment, bundle)
    }

    private fun showProcessing() {
        binding.microLayout.visibility = View.GONE
        binding.progress.visibility = View.VISIBLE
        binding.recordingTitle.text = getText(R.string.home_listen_processing_title)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
