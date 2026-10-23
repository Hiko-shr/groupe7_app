package com.example.groupe7app.model.repository

import androidx.room.withTransaction
import com.example.groupe7app.model.database.AppDatabase
import com.example.groupe7app.model.entity.Gasto
import com.example.groupe7app.model.entity.HistorialEstado
import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.MotivoRechazo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

class GastoRepository(private val db: AppDatabase) {

    private val gastoDao = db.gastoDao()
    private val historialDao = db.historialDao()

    fun deRendicion(rendicionId: Long): Flow<List<Gasto>> = gastoDao.deRendicion(rendicionId)

    suspend fun obtener(id: Long): Gasto? = gastoDao.obtener(id)

    // ---------- Técnico ----------
    // id = 0 es un gasto nuevo (insertar); si ya tiene id, se actualiza
    suspend fun guardar(gasto: Gasto): Long =
        if (gasto.id == 0L) gastoDao.insertar(gasto)
        else { gastoDao.actualizar(gasto); gasto.id }

    suspend fun eliminar(gasto: Gasto) = gastoDao.eliminar(gasto)

    suspend fun rendicionDelDuplicado(rut: String, folio: String, excluirId: Long = 0): String? =
        gastoDao.rendicionDelDuplicado(rut, folio, excluirId)

    suspend fun acumuladoDelDia(usuarioId: Long, categoriaId: Long, fecha: LocalDate, excluirId: Long = 0): Int =
        gastoDao.acumuladoDelDia(usuarioId, categoriaId, fecha, excluirId)

    // ---------- Jefatura y Finanzas ----------
    suspend fun resolver(
        gasto: Gasto, nuevo: EstadoGasto, usuarioId: Long,
        montoAprobado: Int? = null, comentario: String? = null, motivo: MotivoRechazo? = null
    ) = db.withTransaction {
        gastoDao.actualizar(
            gasto.copy(estado = nuevo, montoAprobado = montoAprobado,
                comentarioRevision = comentario, motivoRechazo = motivo)
        )
        historialDao.insertar(
            HistorialEstado(rendicionId = gasto.rendicionId, gastoId = gasto.id, usuarioId = usuarioId,
                estadoAnterior = gasto.estado.name, estadoNuevo = nuevo.name,
                comentario = comentario ?: motivo?.etiqueta, fecha = LocalDateTime.now())
        )
    }
}