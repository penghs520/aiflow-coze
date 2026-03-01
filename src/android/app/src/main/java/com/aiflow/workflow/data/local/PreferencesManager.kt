package com.aiflow.workflow.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "workflow_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = longPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val EMAIL = stringPreferencesKey("email")
        private val POINTS_BALANCE = longPreferencesKey("points_balance")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }
    
    val accessToken: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }
    
    val refreshToken: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]
        }
    
    val userId: Flow<Long?>
        get() = context.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }
    
    val username: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[USERNAME]
        }
    
    val email: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[EMAIL]
        }
    
    val pointsBalance: Flow<Long?>
        get() = context.dataStore.data.map { preferences ->
            preferences[POINTS_BALANCE]
        }
    
    val isLoggedIn: Flow<Boolean>
        get() = context.dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }
    
    suspend fun saveAuthData(
        accessToken: String,
        refreshToken: String,
        userId: Long,
        username: String,
        email: String,
        pointsBalance: Long
    ) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
            preferences[USER_ID] = userId
            preferences[USERNAME] = username
            preferences[EMAIL] = email
            preferences[POINTS_BALANCE] = pointsBalance
            preferences[IS_LOGGED_IN] = true
        }
    }
    
    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
            preferences.remove(USER_ID)
            preferences.remove(USERNAME)
            preferences.remove(EMAIL)
            preferences.remove(POINTS_BALANCE)
            preferences[IS_LOGGED_IN] = false
        }
    }
    
    suspend fun updatePointsBalance(points: Long) {
        context.dataStore.edit { preferences ->
            preferences[POINTS_BALANCE] = points
        }
    }
}