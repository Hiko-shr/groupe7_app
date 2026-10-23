package com.example.groupe7app.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.groupe7app.model.entity.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY nombre")
    fun todas(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun obtener(id: Long): Categoria?

    @Insert
    suspend fun insertarTodas(categorias: List<Categoria>)
}