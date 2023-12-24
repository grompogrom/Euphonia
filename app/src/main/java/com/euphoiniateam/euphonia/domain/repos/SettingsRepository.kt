package com.euphoiniateam.euphonia.domain.repos

import com.euphoiniateam.euphonia.domain.models.Settings

interface SettingsRepository {

    fun saveSettings(saveParam: Settings): Boolean
    fun getSettings(): Settings
}