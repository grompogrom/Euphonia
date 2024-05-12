package com.euphoiniateam.euphonia.domain.usecases

import com.euphoiniateam.euphonia.domain.models.Settings

// TODO: Почему бы не слить с GetSettingsUseCase и возвращать, если нет настроек?
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
