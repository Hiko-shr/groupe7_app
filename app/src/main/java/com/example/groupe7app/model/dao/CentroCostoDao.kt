package com.example.groupe7app.model.dao

import androidx.room.Dao
import androidx.room.Upsert
import androidx.room.Insert
import androidx.room.Query
import com.example.groupe7app.model.entity.CentroCosto
import kotlinx.coroutines.flow.Flow

@Dao
interface CentroCostoDao {
    @Query("SELECT * FROM centros_costo ORDER BY codigo")
    fun todos(): Flow<List<CentroCosto>>

    @Insert
    suspend fun insertarTodos(centros: List<CentroCosto>)

    // ---------- Sincronización con ms-rendiciones ----------
    @Upsert
    suspend fun guardarTodos(centros: List<CentroCosto>)

    @Query("DELETE FROM centros_costo WHERE codigo NOT IN (:codigos)")
    suspend fun eliminarExcepto(codigos: List<String>)
}
