package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "threat_signatures")
data class ThreatSignatureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sha256Hash: String,
    val threatName: String,
    val category: String, // MALWARE, SPYWARE, RANSOMWARE, PYTHON_EXPLOIT
    val severity: String,
    val description: String,
    val dateAdded: Long = System.currentTimeMillis()
)
