package com.example.security.quarantine

import android.content.Context
import com.example.data.database.VipDatabase
import com.example.data.database.entities.QuarantineEntity
import com.example.security.models.FileScanResult
import java.io.File

object QuarantineManager {

    suspend fun quarantineFile(
        context: Context,
        scanResult: FileScanResult
    ): Boolean {
        val targetFile = File(scanResult.filePath)
        if (!targetFile.exists()) return false

        val quarantineDir = File(context.filesDir, "quarantine_vault").apply { if (!exists()) mkdirs() }
        val quarantinedFile = File(quarantineDir, "${scanResult.fileName}_${System.currentTimeMillis()}.vipquarantine")

        return try {
            targetFile.copyTo(quarantinedFile, overwrite = true)
            targetFile.delete() // remove from original location

            val db = VipDatabase.getInstance(context)
            val entity = QuarantineEntity(
                originalFileName = scanResult.fileName,
                originalFilePath = scanResult.filePath,
                quarantinedFilePath = quarantinedFile.absolutePath,
                fileSize = scanResult.fileSize,
                threatName = scanResult.threatName,
                threatScore = scanResult.threatScore,
                severity = scanResult.severity.name,
                detectionReasonsJson = scanResult.reasons.joinToString("; ")
            )
            db.quarantineDao().insertQuarantineItem(entity)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun restoreFile(context: Context, item: QuarantineEntity): Boolean {
        val qFile = File(item.quarantinedFilePath)
        val origFile = File(item.originalFilePath)

        return try {
            if (qFile.exists()) {
                origFile.parentFile?.mkdirs()
                qFile.copyTo(origFile, overwrite = true)
                qFile.delete()
            }
            val db = VipDatabase.getInstance(context)
            db.quarantineDao().deleteQuarantineItem(item)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deletePermanently(context: Context, item: QuarantineEntity): Boolean {
        val qFile = File(item.quarantinedFilePath)
        return try {
            if (qFile.exists()) qFile.delete()
            val db = VipDatabase.getInstance(context)
            db.quarantineDao().deleteQuarantineItem(item)
            true
        } catch (e: Exception) {
            false
        }
    }
}
