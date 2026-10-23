package com.example.groupe7app.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.groupe7app.model.entity.HistorialEstado
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialDao {
    @Query("SELECT * FROM historial_estados WHERE rendicionId = :rendicionId ORDER BY fecha DESC")
    fun deRendicion(rendicionId: Long): Flow<List<HistorialEstado>>

    @Insert
    suspend fun insertar(registro: HistorialEstado)
}