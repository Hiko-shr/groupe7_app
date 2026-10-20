package com.example.groupe7app.data.local.dao

import androidx.room.*
import com.example.groupe7app.data.local.entities.GastoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {
    @Query("SELECT * FROM gastos WHERE rendicionId = :rendicionId")
    fun obtenerGastosPorRendicion(rendicionId: Long): Flow<List<GastoEntity>>

    @Query("SELECT folio FROM gastos")
    suspend fun obtenerTodosLosFolios(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(gasto: GastoEntity): Long

    @Update
    suspend fun actualizar(gasto: GastoEntity)
}