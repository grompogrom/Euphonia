package com.euphoiniateam.euphonia.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

// TODO не вижу необходимости в этих extensions. Они используются по сути в одном месте

suspend fun DataStore<Preferences>.getValueByKey(key: Preferences.Key<*>): Any? {
    val value = this.data
        .map {
            it[key]
        }
    return value.firstOrNull()
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cache")
