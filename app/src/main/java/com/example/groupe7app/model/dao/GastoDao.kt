package com.example.groupe7app.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.groupe7app.model.entity.Gasto
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface GastoDao {
    @Query("SELECT * FROM gastos WHERE rendicionId = :rendicionId ORDER BY fechaDocumento")
    fun deRendicion(rendicionId: Long): Flow<List<Gasto>>

    @Query("SELECT * FROM gastos WHERE id = :id")
    suspend fun obtener(id: Long): Gasto?

    // Duplicados (RN-02): devuelve el número de la rendición donde ya está ese RUT + folio,
    // o null si no existe. excluirId evita que un gasto choque consigo mismo al editarlo.
    @Query("""
        SELECT r.numero FROM gastos g
        INNER JOIN rendiciones r ON r.id = g.rendicionId
        WHERE g.rutEmisor = :rut AND g.folio = :folio AND g.id != :excluirId
        LIMIT 1
    """)
    suspend fun rendicionDelDuplicado(rut: String, folio: String, excluirId: Long = 0): String?

    // Topes diarios (RN-01): cuánto lleva el técnico en esa categoría ese día
    @Query("""
        SELECT COALESCE(SUM(g.montoAdmitido), 0) FROM gastos g
        INNER JOIN rendiciones r ON r.id = g.rendicionId
        WHERE r.usuarioId = :usuarioId AND g.categoriaId = :categoriaId
          AND g.fechaDocumento = :fecha AND g.estado != 'RECHAZADO' AND g.id != :excluirId
    """)
    suspend fun acumuladoDelDia(usuarioId: Long, categoriaId: Long, fecha: LocalDate, excluirId: Long = 0): Int

    @Insert
    suspend fun insertar(gasto: Gasto): Long

    @Insert
    suspend fun insertarTodos(gastos: List<Gasto>)

    @Update
    suspend fun actualizar(gasto: Gasto)

    @Delete
    suspend fun eliminar(gasto: Gasto)
}