package com.euphoiniateam.euphonia.ui.settings

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.data.repos.SettingsRepositoryImpl
import com.euphoiniateam.euphonia.databinding.FragmentSettingsBinding
import com.euphoiniateam.euphonia.domain.models.Settings
import com.euphoiniateam.euphonia.domain.usecases.GetDefaultSettingsUseCase
import com.euphoiniateam.euphonia.domain.usecases.GetSettingsUseCase
import com.euphoiniateam.euphonia.domain.usecases.SaveSettingsUseCase
import com.euphoiniateam.euphonia.ui.creation.CreationViewModel
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel

//    val switchHistory : SwitchMaterial = binding.switchHistory
//    val switchRecordingAudio : SwitchMaterial = binding.switchRecordingAudio
//    val switchRecordingStave : SwitchMaterial = binding.switchRecordingStave
//    val sliderPianoSize : Slider = binding.sliderPianoSize
//    val sliderStaveSize : Slider = binding.sliderStaveSize
//    val switchShowingStave :SwitchMaterial = binding.switchShowingStave

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

        binding.saveBtn.setOnClickListener {
            viewModel.saveSettings()
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
        viewModel.settingsLiveData.observe(viewLifecycleOwner) {settings ->
            binding.sliderPianoSize.value = settings.piano_size
            binding.switchHistory.isChecked = settings.history
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}