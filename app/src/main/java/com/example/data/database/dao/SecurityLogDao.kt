package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.entities.SecurityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityLogDao {
    @Query("SELECT * FROM security_logs ORDER BY timestamp DESC LIMIT 200")
    fun getAllLogs(): Flow<List<SecurityLogEntity>>

    @Query("SELECT * FROM security_logs WHERE severity = :severity ORDER BY timestamp DESC")
    fun getLogsBySeverity(severity: String): Flow<List<SecurityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SecurityLogEntity)

    @Query("DELETE FROM security_logs")
    suspend fun clearLogs()
}
