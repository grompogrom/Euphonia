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
import com.euphoiniateam.euphonia.data.repos.SettingsRepositoryImpl
import com.euphoiniateam.euphonia.databinding.FragmentSettingsBinding
import com.euphoiniateam.euphonia.domain.models.Settings
import com.euphoiniateam.euphonia.domain.usecases.GetDefaultSettingsUseCase
import com.euphoiniateam.euphonia.domain.usecases.GetSettingsUseCase
import com.euphoiniateam.euphonia.domain.usecases.SaveSettingsUseCase
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val settingsRepository by lazy { SettingsRepositoryImpl(context = requireContext()) }
    private val getDefaultSettingsUseCase = GetDefaultSettingsUseCase()
    private val getSettingsUseCase by lazy { GetSettingsUseCase(settingsRepository = settingsRepository) }
    private val saveSettingsUseCase by lazy { SaveSettingsUseCase(settingsRepository = settingsRepository) }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root



        val text = "Settings saved"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, text, duration)

        val switchHistory : SwitchMaterial = binding.switchHistory
        val switchRecordingAudio : SwitchMaterial = binding.switchRecordingAudio
        val switchRecordingStave : SwitchMaterial = binding.switchRecordingStave
        val sliderPianoSize : Slider = binding.sliderPianoSize
        val sliderStaveSize : Slider = binding.sliderStaveSize
        val switchShowingStave :SwitchMaterial = binding.switchShowingStave

        switchHistory.isChecked = getSettingsUseCase.execute().history
        switchRecordingAudio.isChecked = getSettingsUseCase.execute().recording_audio
        switchRecordingStave.isChecked = getSettingsUseCase.execute().recording_stave
        sliderPianoSize.value = getSettingsUseCase.execute().piano_size
        sliderStaveSize.value = getSettingsUseCase.execute().stave_size
        switchShowingStave.isChecked = getSettingsUseCase.execute().showing_stave

        val btnSave: Button = binding.saveBtn
        btnSave.setOnClickListener {
            val param1 = switchHistory.isChecked
            val param2 = switchRecordingAudio.isChecked
            val param3 = switchRecordingStave.isChecked
            val param4 = sliderPianoSize.value
            val param5 = sliderStaveSize.value
            val param6 = switchShowingStave.isChecked

            val params = Settings(param1, param2, param3, param4, param5, param6)
            val result: Boolean = saveSettingsUseCase.execute(params)
            toast.show()
        }

        val btnDefault: Button = binding.defaultBtn
        btnDefault.setOnClickListener {
            val defaultSettings = getDefaultSettingsUseCase.execute()
            switchHistory.isChecked = defaultSettings.history
            switchRecordingAudio.isChecked = defaultSettings.recording_audio
            switchRecordingStave.isChecked = defaultSettings.recording_stave
            sliderPianoSize.value = defaultSettings.piano_size
            sliderStaveSize.value = defaultSettings.stave_size
            switchShowingStave.isChecked = defaultSettings.showing_stave
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}