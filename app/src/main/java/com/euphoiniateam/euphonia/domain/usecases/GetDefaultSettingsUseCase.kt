package com.euphoiniateam.euphonia.domain.usecases

import com.euphoiniateam.euphonia.domain.models.Settings

class GetDefaultSettingsUseCase {
    fun execute(): Settings {
        return Settings(
            true,
            true,
            true,
            7f,
            5f,
            true
        )
    }
}
