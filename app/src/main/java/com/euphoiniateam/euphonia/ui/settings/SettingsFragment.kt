package com.euphoiniateam.euphonia.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentSettingsBinding
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val text = getString(R.string.settings_saved)
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, text, duration)

        val switchHistory: SwitchMaterial = binding.switchHistory
        val switchRecordingAudio: SwitchMaterial = binding.switchRecordingAudio
        val switchRecordingStave: SwitchMaterial = binding.switchRecordingStave
        val sliderPianoSize: Slider = binding.sliderPianoSize
        val sliderStaveSize: Slider = binding.sliderStaveSize
        val switchShowingStave: SwitchMaterial = binding.switchShowingStave

        binding.saveBtn.setOnClickListener {
            viewModel.saveSettings(
                switchHistory.isChecked,
                switchRecordingAudio.isChecked,
                switchRecordingStave.isChecked,
                sliderPianoSize.value,
                sliderStaveSize.value,
                switchShowingStave.isChecked
            )
            toast.show()
        }

        binding.defaultBtn.setOnClickListener {
            viewModel.defaultSettings()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, SettingsViewModel.provideFactory(requireContext()))
            .get(SettingsViewModel::class.java)
        viewModel.settingsLiveData.observe(viewLifecycleOwner) { settings ->
            binding.sliderPianoSize.value = settings.piano_size
            binding.switchHistory.isChecked = settings.history
            binding.switchRecordingAudio.isChecked = settings.recording_audio
            binding.switchRecordingStave.isChecked = settings.recording_stave
            binding.sliderStaveSize.value = settings.stave_size
            binding.switchShowingStave.isChecked = settings.showing_stave
        }
        // viewModel.loadSettings()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
