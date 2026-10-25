package com.example.groupe7app.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.groupe7app.model.entity.Gasto
import com.example.groupe7app.model.enums.EstadoRendicion
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface GastoDao {
    @Query("SELECT * FROM gastos WHERE rendicionId = :rendicionId ORDER BY fechaDocumento")
    fun deRendicion(rendicionId: Long): Flow<List<Gasto>>

    // Mis rendiciones / Inicio técnico: todos los gastos de las rendiciones de un técnico
    @Query("""
        SELECT g.* FROM gastos g
        INNER JOIN rendiciones r ON r.id = g.rendicionId
        WHERE r.usuarioId = :usuarioId
    """)
    fun deUsuario(usuarioId: Long): Flow<List<Gasto>>

    // Bandeja de jefatura: gastos de las rendiciones de los técnicos de su equipo
    @Query("""
        SELECT g.* FROM gastos g
        INNER JOIN rendiciones r ON r.id = g.rendicionId
        INNER JOIN usuarios u ON u.id = r.usuarioId
        WHERE u.jefaturaId = :jefaturaId
    """)
    fun deEquipo(jefaturaId: Long): Flow<List<Gasto>>

    // Finanzas: gastos de todas las rendiciones que están en un estado (APROBADA_JEFATURA o APROBADA_FINANZAS)
    @Query("""
        SELECT g.* FROM gastos g
        INNER JOIN rendiciones r ON r.id = g.rendicionId
        WHERE r.estado = :estado
    """)
    fun deRendicionesEnEstado(estado: EstadoRendicion): Flow<List<Gasto>>

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

    // Finanzas confirma los gastos que la jefatura aprobó
    @Query("UPDATE gastos SET estado = 'APROBADO_FINAL' WHERE rendicionId = :rendicionId AND estado = 'APROBADO'")
    suspend fun marcarAprobadoFinal(rendicionId: Long)

    @Query("UPDATE gastos SET estado = 'ENVIADO' WHERE rendicionId = :rendicionId AND estado IN ('BORRADOR', 'OBSERVADO')")
    suspend fun marcarEnviados(rendicionId: Long)
    @Insert
    suspend fun insertar(gasto: Gasto): Long

    @Insert
    suspend fun insertarTodos(gastos: List<Gasto>)

    @Update
    suspend fun actualizar(gasto: Gasto)

    @Delete
    suspend fun eliminar(gasto: Gasto)
}