package com.euphoiniateam.euphonia.domain.repos

import com.euphoiniateam.euphonia.domain.models.Settings

interface SettingsRepository {

    suspend fun saveSettings(saveParam: Settings): Boolean
    suspend fun getSettings(): Settings
}
