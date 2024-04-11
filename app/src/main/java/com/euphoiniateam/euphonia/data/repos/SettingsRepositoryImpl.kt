package com.euphoiniateam.euphonia.data.repos

import android.content.Context
import com.euphoiniateam.euphonia.domain.models.Settings
import com.euphoiniateam.euphonia.domain.repos.SettingsRepository

private const val SHARED_PREFS_SETTINGS = "shared_prefs_settings"
private const val KEY_HISTORY = "history"
private const val KEY_RECORDING_AUDIO = "recording_audio"
private const val KEY_RECORDING_STAVE = "recording_stave"
private const val KEY_PIANO_SIZE = "piano_size"
private const val KEY_STAVE_SIZE = "stave_size"
private const val KEY_SHOWING_STAVE = "showing_stave"
class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFS_SETTINGS,
        Context.MODE_PRIVATE
    )

    override suspend fun saveSettings(saveParam: Settings): Boolean {

        sharedPreferences.edit().putBoolean(KEY_HISTORY, saveParam.history).apply()
        sharedPreferences.edit().putBoolean(KEY_RECORDING_AUDIO, saveParam.recording_audio).apply()
        sharedPreferences.edit().putBoolean(KEY_RECORDING_STAVE, saveParam.recording_stave).apply()
        sharedPreferences.edit().putFloat(KEY_PIANO_SIZE, saveParam.piano_size).apply()
        sharedPreferences.edit().putFloat(KEY_STAVE_SIZE, saveParam.stave_size).apply()
        sharedPreferences.edit().putBoolean(KEY_SHOWING_STAVE, saveParam.showing_stave).apply()

        return true
    }

    override suspend fun getSettings(): Settings {

        val history = sharedPreferences.getBoolean(KEY_HISTORY, true)
        val recordingAudio = sharedPreferences.getBoolean(KEY_RECORDING_AUDIO, true)
        val recordingStave = sharedPreferences.getBoolean(KEY_RECORDING_STAVE, true)
        val pianoSize = sharedPreferences.getFloat(KEY_PIANO_SIZE, 7f)
        val staveSize = sharedPreferences.getFloat(KEY_STAVE_SIZE, 5f)
        val showingStave = sharedPreferences.getBoolean(KEY_SHOWING_STAVE, true)

        return Settings(history, recordingAudio, recordingStave, pianoSize, staveSize, showingStave)
    }
}
