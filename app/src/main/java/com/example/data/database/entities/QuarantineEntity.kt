package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quarantine_items")
data class QuarantineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalFileName: String,
    val originalFilePath: String,
    val quarantinedFilePath: String,
    val fileSize: Long,
    val threatName: String,
    val threatScore: Int,
    val severity: String,
    val detectionReasonsJson: String,
    val quarantineTimestamp: Long = System.currentTimeMillis()
)
