package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.database.dao.QuarantineDao
import com.example.data.database.dao.SecurityLogDao
import com.example.data.database.dao.ThreatSignatureDao
import com.example.data.database.dao.VaultDao
import com.example.data.database.entities.QuarantineEntity
import com.example.data.database.entities.SecurityLogEntity
import com.example.data.database.entities.ThreatSignatureEntity
import com.example.data.database.entities.VaultEntity

@Database(
    entities = [
        SecurityLogEntity::class,
        QuarantineEntity::class,
        VaultEntity::class,
        ThreatSignatureEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VipDatabase : RoomDatabase() {
    abstract fun securityLogDao(): SecurityLogDao
    abstract fun quarantineDao(): QuarantineDao
    abstract fun vaultDao(): VaultDao
    abstract fun threatSignatureDao(): ThreatSignatureDao

    companion object {
        private const val TAG = "VipDatabase"

        @Volatile
        private var INSTANCE: VipDatabase? = null

        fun getInstance(context: Context): VipDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): VipDatabase {
            val appContext = context.applicationContext
            return try {
                Room.databaseBuilder(
                    appContext,
                    VipDatabase::class.java,
                    "vip_protection_db"
                )
                .fallbackToDestructiveMigration()
                .build()
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    Room.inMemoryDatabaseBuilder(
                        appContext,
                        VipDatabase::class.java
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                } catch (e2: Exception) {
                    e2.printStackTrace()
                    throw e2
                }
            }
        }
    }
}
