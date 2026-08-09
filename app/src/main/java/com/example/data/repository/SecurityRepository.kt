package com.example.data.repository

import android.content.Context
import com.example.data.database.VipDatabase
import com.example.data.database.entities.QuarantineEntity
import com.example.data.database.entities.SecurityLogEntity
import com.example.data.database.entities.VaultEntity
import com.example.data.preferences.SecurityPreferences
import com.example.security.models.FileScanResult
import com.example.security.quarantine.QuarantineManager
import com.example.security.scanner.FullDeviceScanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class SecurityRepository(private val context: Context) {

    private val db = VipDatabase.getInstance(context)
    val preferences = SecurityPreferences(context)

    val allLogs: Flow<List<SecurityLogEntity>> = db.securityLogDao().getAllLogs()
        .catch { emit(emptyList()) }

    val allQuarantineItems: Flow<List<QuarantineEntity>> = db.quarantineDao().getAllQuarantineItems()
        .catch { emit(emptyList()) }

    val allVaultItems: Flow<List<VaultEntity>> = db.vaultDao().getAllVaultItems()
        .catch { emit(emptyList()) }

    suspend fun logEvent(title: String, details: String, severity: String, category: String, target: String = "", action: String = "LOGGED") {
        try {
            db.securityLogDao().insertLog(
                SecurityLogEntity(
                    title = title,
                    details = details,
                    severity = severity,
                    actionTaken = action,
                    category = category,
                    targetItem = target
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun quarantineFile(scanResult: FileScanResult): Boolean {
        return try {
            val success = QuarantineManager.quarantineFile(context, scanResult)
            if (success) {
                logEvent(
                    title = "File Quarantined",
                    details = "Isolated ${scanResult.fileName} due to ${scanResult.threatName}",
                    severity = scanResult.severity.name,
                    category = "QUARANTINE",
                    target = scanResult.fileName,
                    action = "QUARANTINED"
                )
            }
            success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun restoreQuarantineItem(item: QuarantineEntity): Boolean {
        return try {
            QuarantineManager.restoreFile(context, item)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteQuarantineItemPermanently(item: QuarantineEntity): Boolean {
        return try {
            QuarantineManager.deletePermanently(context, item)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun saveVaultItem(item: VaultEntity) {
        try {
            db.vaultDao().insertVaultItem(item)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteVaultItem(item: VaultEntity) {
        try {
            db.vaultDao().deleteVaultItem(item)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearAllLogs() {
        try {
            db.securityLogDao().clearLogs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
