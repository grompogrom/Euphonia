package com.euphoiniateam.euphonia.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.euphoiniateam.euphonia.R
import com.euphoiniateam.euphonia.databinding.FragmentSettingsBinding
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel
//    private lateinit var authLauncher: ActivityResultLauncher<Collection<VKScope>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        authLauncher = VK.login(requireActivity()) { result : VKAuthenticationResult ->
//            when (result) {
//                is VKAuthenticationResult.Success -> {
//                    Toast.makeText(context, "YESYESYESYES", Toast.LENGTH_SHORT).show()
//                }
//                is VKAuthenticationResult.Failed -> {
//                    Toast.makeText(context, "NONONONO", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

//        binding.vkBtn.setOnClickListener {
//            authLauncher.launch(arrayListOf(VKScope.WALL, VKScope.PHOTOS))
//            VK.addTokenExpiredHandler(tokenTracker)
//            requestUser()
//        }

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

//    private val tokenTracker = object: VKTokenExpiredHandler {
//        override fun onTokenExpired() {
//            // token expired
//        }
//    }

//    private fun requestUser() {
//        VK.execute(VKUsersCommand(), object: VKApiCallback<List<VKUser>> {
//            override fun success(result: List<VKUser>) {
//                if (result.isNotEmpty()) {
//                    val user = result[0]
//                    val username = "${user.firstName} ${user.lastName}"
//                    Toast.makeText(context, "Welcome, $username!", Toast.LENGTH_SHORT).show()
//                }
//            }
//            override fun fail(error: Exception) {
//                Log.e("VKRequest", error.toString())
//            }
//        })
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, SettingsViewModel.provideFactory(requireContext()))
            .get(SettingsViewModel::class.java)
        lifecycleScope.launch {
            viewModel.settingsStateFlow.collectLatest { settings ->
                binding.sliderPianoSize.value = settings.piano_size
                binding.switchHistory.isChecked = settings.history
                binding.switchRecordingAudio.isChecked = settings.recording_audio
                binding.switchRecordingStave.isChecked = settings.recording_stave
                binding.sliderStaveSize.value = settings.stave_size
                binding.switchShowingStave.isChecked = settings.showing_stave
            }
        }
        // viewModel.loadSettings()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
