package com.euphoiniateam.euphonia.ui.record

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.euphoiniateam.euphonia.data.repos.AudioToMidiRepositoryImpl
import com.euphoiniateam.euphonia.data.source.convert.ConvertDataSourceImp
import com.euphoiniateam.euphonia.domain.ConvertingException
import com.euphoiniateam.euphonia.domain.repos.AudioToMidiRepository
import com.euphoiniateam.euphonia.tools.MicrophoneFunctions
import com.euphoiniateam.euphonia.tools.getMicroOutputFile
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecordViewModel(
    val audioToMidiRepository: AudioToMidiRepository
) : ViewModel() {
    val screenState: MutableStateFlow<RecordScreenState> =
        MutableStateFlow(RecordScreenState.RECORDING)
    private var microResultFile: File? = null

    fun startRecording(applicationContext: Context) {
        microResultFile = getMicroOutputFile(applicationContext)
        MicrophoneFunctions.startRecording(applicationContext, microResultFile!!)
    }

    fun stopRecording() {
        MicrophoneFunctions.stopRecording()
        viewModelScope.launch(Dispatchers.IO) {
            microResultFile?.let {
                screenState.emit(RecordScreenState.PROCESS)
                try {
                    val midi = audioToMidiRepository.convertMp3ToMidi(it.toUri())
                    screenState.emit(RecordScreenState.SUCCESS(midi))
                } catch (exception: ConvertingException) {
                    screenState.emit(
                        RecordScreenState.ERROR(
                            exception.message ?: "Unknowing exception"
                        )
                    )
                }
            } ?: screenState.emit(RecordScreenState.ERROR("micro not available"))
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RecordViewModel(
                    audioToMidiRepository = AudioToMidiRepositoryImpl(
                        dataSource = ConvertDataSourceImp(context)
                    )
                )
            }
        }
    }
}

sealed class RecordScreenState {
    data object RECORDING : RecordScreenState()
    data object PROCESS : RecordScreenState()

    class SUCCESS(val uri: Uri) : RecordScreenState()
    class ERROR(val description: String) : RecordScreenState()
}
