package com.euphoiniateam.euphonia.domain.usecases

import com.euphoiniateam.euphonia.domain.models.Settings
import com.euphoiniateam.euphonia.domain.repos.SettingsRepository

class GetSettingsUseCase(private val settingsRepository: SettingsRepository) {

    fun execute(): Settings {
        return settingsRepository.getSettings()
    }
}