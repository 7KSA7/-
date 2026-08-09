package com.example.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_logs")
data class SecurityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val details: String,
    val severity: String, // SAFE, SUSPICIOUS, DANGEROUS, CRITICAL
    val actionTaken: String,
    val category: String, // MALWARE, NETWORK, PRIVACY, PYTHON, VAULT, QUARANTINE, SYSTEM
    val targetItem: String = ""
)
