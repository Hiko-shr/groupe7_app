package com.example.groupe7app.model.dao

import androidx.room.Dao
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
}