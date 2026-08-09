package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.entities.VaultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_items ORDER BY encryptionTimestamp DESC")
    fun getAllVaultItems(): Flow<List<VaultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultItem(item: VaultEntity): Long

    @Delete
    suspend fun deleteVaultItem(item: VaultEntity)

    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
