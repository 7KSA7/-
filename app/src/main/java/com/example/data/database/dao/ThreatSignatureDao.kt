package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.entities.ThreatSignatureEntity

@Dao
interface ThreatSignatureDao {
    @Query("SELECT * FROM threat_signatures WHERE sha256Hash = :hash LIMIT 1")
    suspend fun getSignatureByHash(hash: String): ThreatSignatureEntity?

    @Query("SELECT COUNT(*) FROM threat_signatures")
    suspend fun getSignatureCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignatures(signatures: List<ThreatSignatureEntity>)
}
