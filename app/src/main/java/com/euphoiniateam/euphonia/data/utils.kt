package com.euphoiniateam.euphonia.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

suspend fun DataStore<Preferences>.getValueByKey(key: Preferences.Key<*>): Any? {
    val value = this.data
        .map {
            it[key]
        }
    return value.firstOrNull()
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cache")
