package com.euphoiniateam.euphonia.data.repos

import android.content.Context
import com.euphoiniateam.euphonia.domain.models.Settings
import com.euphoiniateam.euphonia.domain.repos.SettingsRepository

private const val SHARED_PREFS_SETTINGS = "shared_prefs_settings"
private const val KEY_HISTORY = "history"
private const val KEY_RECORDING_AUDIO = "recording_audio"
private const val KEY_RECORDING_STAVE = "recording_stave"
private const val KEY_PIANO_SIZE = "piano_size"
private const val KEY_NOTES_AMOUNT = "notes_gen_amount"
private const val KEY_STAVE_ON = "stave_on"
class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFS_SETTINGS,
        Context.MODE_PRIVATE
    )

    override suspend fun saveSettings(saveParam: Settings): Boolean {

        sharedPreferences.edit().putBoolean(KEY_HISTORY, saveParam.history).apply()
        sharedPreferences.edit().putBoolean(KEY_RECORDING_AUDIO, saveParam.recordingAudio).apply()
        sharedPreferences.edit().putBoolean(KEY_RECORDING_STAVE, saveParam.recordingStave).apply()
        sharedPreferences.edit().putFloat(KEY_PIANO_SIZE, saveParam.pianoSize).apply()
        sharedPreferences.edit().putInt(KEY_NOTES_AMOUNT, saveParam.notesAmount).apply()
        sharedPreferences.edit().putBoolean(KEY_STAVE_ON, saveParam.staveOn).apply()

        return true
    }

    override suspend fun getSettings(): Settings {

        val history = sharedPreferences.getBoolean(KEY_HISTORY, true)
        val recordingAudio = sharedPreferences.getBoolean(KEY_RECORDING_AUDIO, true)
        val recordingStave = sharedPreferences.getBoolean(KEY_RECORDING_STAVE, true)
        val pianoSize = sharedPreferences.getFloat(KEY_PIANO_SIZE, 10f)
        val notesAmount = sharedPreferences.getInt(KEY_NOTES_AMOUNT, 5)
        val staveOn = sharedPreferences.getBoolean(KEY_STAVE_ON, true)

        return Settings(history, recordingAudio, recordingStave, pianoSize, notesAmount, staveOn)
    }
}
