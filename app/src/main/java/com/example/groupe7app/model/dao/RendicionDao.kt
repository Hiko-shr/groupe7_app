package com.example.groupe7app.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.groupe7app.model.entity.Rendicion
import com.example.groupe7app.model.enums.EstadoRendicion
import kotlinx.coroutines.flow.Flow

@Dao
interface RendicionDao {
    // Técnico: sus rendiciones, la más nueva primero
    @Query("SELECT * FROM rendiciones WHERE usuarioId = :usuarioId ORDER BY id DESC")
    fun misRendiciones(usuarioId: Long): Flow<List<Rendicion>>

    @Query("SELECT * FROM rendiciones WHERE id = :id")
    fun obtener(id: Long): Flow<Rendicion?>

    // Jefatura: rendiciones ENVIADAS por los técnicos de su equipo, la más antigua primero
    @Query("""
        SELECT r.* FROM rendiciones r
        INNER JOIN usuarios u ON u.id = r.usuarioId
        WHERE u.jefaturaId = :jefaturaId AND r.estado = 'ENVIADA'
        ORDER BY r.fechaEnvio ASC
    """)
    fun bandejaJefatura(jefaturaId: Long): Flow<List<Rendicion>>

    // Finanzas: rendiciones en un estado (APROBADA_JEFATURA o APROBADA_FINANZAS)
    @Query("SELECT * FROM rendiciones WHERE estado = :estado ORDER BY fechaEnvio ASC")
    fun porEstado(estado: EstadoRendicion): Flow<List<Rendicion>>

    // Para armar el número correlativo RND-2026-000001
    @Query("SELECT COUNT(*) FROM rendiciones")
    suspend fun contar(): Int

    @Insert
    suspend fun insertar(rendicion: Rendicion): Long

    @Update
    suspend fun actualizar(rendicion: Rendicion)

    @Delete
    suspend fun eliminar(rendicion: Rendicion)
}