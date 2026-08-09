package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.entities.QuarantineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuarantineDao {
    @Query("SELECT * FROM quarantine_items ORDER BY quarantineTimestamp DESC")
    fun getAllQuarantineItems(): Flow<List<QuarantineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuarantineItem(item: QuarantineEntity): Long

    @Delete
    suspend fun deleteQuarantineItem(item: QuarantineEntity)

    @Query("DELETE FROM quarantine_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
