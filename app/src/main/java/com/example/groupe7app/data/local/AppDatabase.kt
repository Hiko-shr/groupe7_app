package com.example.groupe7app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.groupe7app.data.local.dao.GastoDao
import com.example.groupe7app.data.local.dao.RendicionDao
import com.example.groupe7app.data.local.entities.GastoEntity
import com.example.groupe7app.data.local.entities.RendicionEntity

@Database(entities = [RendicionEntity::class, GastoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rendicionDao(): RendicionDao
    abstract fun gastoDao(): GastoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "teqgastos_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}