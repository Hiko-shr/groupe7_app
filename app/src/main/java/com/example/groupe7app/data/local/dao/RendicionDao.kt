package com.example.groupe7app.data.local.dao

import androidx.room.*
import com.example.groupe7app.data.local.entities.RendicionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RendicionDao {
    @Query("SELECT * FROM rendiciones ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<RendicionEntity>>

    @Query("SELECT * FROM rendiciones WHERE id = :id")
    suspend fun obtenerPorId(id: Long): RendicionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(rendicion: RendicionEntity): Long

    @Update
    suspend fun actualizar(rendicion: RendicionEntity)
}