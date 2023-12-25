package com.euphoiniateam.euphonia.domain.usecases

import com.euphoiniateam.euphonia.domain.models.Settings
import com.euphoiniateam.euphonia.domain.repos.SettingsRepository

class SaveSettingsUseCase(private val settingsRepository: SettingsRepository) {
    fun execute(param: Settings) : Boolean {
        val result: Boolean = settingsRepository.saveSettings(saveParam = param)
        return result
    }
}