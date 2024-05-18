package com.euphoiniateam.euphonia.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.euphoiniateam.euphonia.MainActivity
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentSettingsBinding
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel
    private var loadingDialog: DialogLoadingFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, SettingsViewModel.provideFactory(requireContext()))
            .get(SettingsViewModel::class.java)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val authLauncher = (activity as MainActivity).authLauncher

        binding.vkBtn.setOnClickListener {
            authLauncher.launch(arrayListOf(VKScope.WALL, VKScope.PHOTOS))
        }
        Log.d("MyState", "${viewModel.screenStateFlow.value}")
        binding.generateVkBtn.setOnClickListener {
            if (VK.isLoggedIn()) {
                viewModel.processProfile()
            } else {
                authLauncher.launch(arrayListOf(VKScope.WALL, VKScope.PHOTOS))
            }
        }

        val text = getString(R.string.settings_saved)
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, text, duration)

        val switchHistory: SwitchMaterial = binding.switchHistory
        val switchRecordingAudio: SwitchMaterial = binding.switchRecordingAudio
        val switchRecordingStave: SwitchMaterial = binding.switchRecordingStave
        val sliderPianoSize: Slider = binding.sliderPianoSize
        val sliderNotesAmount: Slider = binding.sliderNotesAmount
        val switchStaveOn: SwitchMaterial = binding.switchStaveOn

        binding.saveBtn.setOnClickListener {
            viewModel.saveSettings(
                switchHistory.isChecked,
                switchRecordingAudio.isChecked,
                switchRecordingStave.isChecked,
                sliderPianoSize.value,
                sliderNotesAmount.value,
                switchStaveOn.isChecked
            )
            toast.show()
        }

        binding.defaultBtn.setOnClickListener {
            viewModel.defaultSettings()
        }
        subscribeOnScreenState()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        lifecycleScope.launch {
            viewModel.settingsStateFlow.collectLatest { settings ->
                binding.sliderPianoSize.value = settings.pianoSize
                binding.switchHistory.isChecked = settings.history
                binding.switchRecordingAudio.isChecked = settings.recordingAudio
                binding.switchRecordingStave.isChecked = settings.recordingStave
                binding.sliderNotesAmount.value = settings.notesAmount
                binding.switchStaveOn.isChecked = settings.staveOn
            }
        }

        // viewModel.loadSettings()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun subscribeOnScreenState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenStateFlow.collect {
                when (it) {
                    VkLoadingState.EMPTY -> {}
                    VkLoadingState.LOADING -> {
                        loadingDialog = DialogLoadingFragment().apply {
                            show(
                                this@SettingsFragment.parentFragmentManager,
                                DialogLoadingFragment.TAG
                            )
                        }
                    }
                    VkLoadingState.SUCCESS -> {
                        loadingDialog?.dismiss()
                        loadingDialog = null
                        viewModel.endProfileProcess()
                        val action = SettingsFragmentDirections
                            .actionNavigationNotificationsToCreationFragment()
                        findNavController().navigate(action)
                        Log.d("MyState", "$it")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
