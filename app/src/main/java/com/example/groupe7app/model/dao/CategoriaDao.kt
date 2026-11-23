package com.example.groupe7app.model.dao

import androidx.room.Dao
import androidx.room.Upsert
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

    // ---------- Sincronización con ms-rendiciones ----------
    @Upsert
    suspend fun guardarTodas(categorias: List<Categoria>)

    // Borra las que ya no existen en el servidor
    @Query("DELETE FROM categorias WHERE id NOT IN (:ids)")
    suspend fun eliminarExcepto(ids: List<Long>)
}
