package com.example.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "vip_security_prefs")

class SecurityPreferences(private val context: Context) {

    companion object {
        val ABSOLUTE_PROTECTION = booleanPreferencesKey("absolute_protection")
        val REALTIME_PROTECTION = booleanPreferencesKey("realtime_protection")
        val NETWORK_PROTECTION = booleanPreferencesKey("network_protection")
        val PRIVACY_SHIELD = booleanPreferencesKey("privacy_shield")
        val ANTI_SPYWARE = booleanPreferencesKey("anti_spyware")
        val AUTO_SCAN_DOWNLOADS = booleanPreferencesKey("auto_scan_downloads")
        val EMERGENCY_LOCKDOWN = booleanPreferencesKey("emergency_lockdown")
        val APP_LANGUAGE = stringPreferencesKey("app_language") // ar, en, fr
        val VAULT_PIN_HASH = stringPreferencesKey("vault_pin_hash")
    }

    val isAbsoluteProtectionEnabled: Flow<Boolean> = context.dataStore.data.map { it[ABSOLUTE_PROTECTION] ?: true }.catch { emit(true) }
    val isRealtimeProtectionEnabled: Flow<Boolean> = context.dataStore.data.map { it[REALTIME_PROTECTION] ?: true }.catch { emit(true) }
    val isNetworkProtectionEnabled: Flow<Boolean> = context.dataStore.data.map { it[NETWORK_PROTECTION] ?: false }.catch { emit(false) }
    val isPrivacyShieldEnabled: Flow<Boolean> = context.dataStore.data.map { it[PRIVACY_SHIELD] ?: true }.catch { emit(true) }
    val isAntiSpywareEnabled: Flow<Boolean> = context.dataStore.data.map { it[ANTI_SPYWARE] ?: true }.catch { emit(true) }
    val isAutoScanDownloadsEnabled: Flow<Boolean> = context.dataStore.data.map { it[AUTO_SCAN_DOWNLOADS] ?: true }.catch { emit(true) }
    val isEmergencyLockdownActive: Flow<Boolean> = context.dataStore.data.map { it[EMERGENCY_LOCKDOWN] ?: false }.catch { emit(false) }
    val currentLanguage: Flow<String> = context.dataStore.data.map { it[APP_LANGUAGE] ?: "ar" }.catch { emit("ar") }
    val vaultPinHash: Flow<String?> = context.dataStore.data.map { it[VAULT_PIN_HASH] }.catch { emit(null) }

    suspend fun setAbsoluteProtection(enabled: Boolean) {
        try { context.dataStore.edit { it[ABSOLUTE_PROTECTION] = enabled } } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun setRealtimeProtection(enabled: Boolean) {
        try { context.dataStore.edit { it[REALTIME_PROTECTION] = enabled } } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun setNetworkProtection(enabled: Boolean) {
        try { context.dataStore.edit { it[NETWORK_PROTECTION] = enabled } } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun setPrivacyShield(enabled: Boolean) {
        try { context.dataStore.edit { it[PRIVACY_SHIELD] = enabled } } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun setAntiSpyware(enabled: Boolean) {
        try { context.dataStore.edit { it[ANTI_SPYWARE] = enabled } } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun setAutoScanDownloads(enabled: Boolean) {
        try { context.dataStore.edit { it[AUTO_SCAN_DOWNLOADS] = enabled } } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun setEmergencyLockdown(active: Boolean) {
        try { context.dataStore.edit { it[EMERGENCY_LOCKDOWN] = active } } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun setLanguage(lang: String) {
        try { context.dataStore.edit { it[APP_LANGUAGE] = lang } } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun setVaultPinHash(hash: String) {
        try { context.dataStore.edit { it[VAULT_PIN_HASH] = hash } } catch (e: Exception) { e.printStackTrace() }
    }
}
