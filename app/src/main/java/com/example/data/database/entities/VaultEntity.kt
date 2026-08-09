package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_items")
data class VaultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalName: String,
    val encryptedName: String,
    val encryptedPath: String,
    val originalPath: String,
    val fileSize: Long,
    val mimeType: String,
    val encryptionTimestamp: Long = System.currentTimeMillis()
)
