package com.euphoiniateam.euphonia.data.source.stave

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.euphoiniateam.euphonia.data.getValueByKey
import com.euphoiniateam.euphonia.data.models.LocalStave
import com.google.gson.Gson

// TODO: обычное File api тут лучше подойдет
// TODO: реализации пометить как internal
class StaveCache(
    private val dataStore: DataStore<Preferences>
) : StaveLocalDataStore {

    companion object {
        val LOCALSTAVE_KEY = stringPreferencesKey("localstave")
    }

    override suspend fun loadData(): LocalStave {
        val jsonData = dataStore.getValueByKey(LOCALSTAVE_KEY) as? String ?: ""
        return Gson().fromJson(jsonData, LocalStave::class.java)
    }

    override suspend fun saveData(stave: LocalStave) {
        val jsonString = Gson().toJson(stave)
        dataStore.edit { preferences ->
            preferences[LOCALSTAVE_KEY] = jsonString
        }
    }
}
