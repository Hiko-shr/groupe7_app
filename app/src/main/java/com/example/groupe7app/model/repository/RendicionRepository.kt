package com.example.groupe7app.model.repository

import androidx.room.withTransaction
import com.example.groupe7app.model.database.AppDatabase
import com.example.groupe7app.model.entity.HistorialEstado
import com.example.groupe7app.model.entity.Pago
import com.example.groupe7app.model.entity.Rendicion
import com.example.groupe7app.model.enums.EstadoRendicion
import java.time.LocalDate
import java.time.LocalDateTime

class RendicionRepository(private val db: AppDatabase) {

    private val rendicionDao = db.rendicionDao()
    private val gastoDao = db.gastoDao()
    private val historialDao = db.historialDao()
    private val pagoDao = db.pagoDao()

    // ---------- Lecturas ----------
    fun misRendiciones(usuarioId: Long) = rendicionDao.misRendiciones(usuarioId)
    fun obtener(id: Long) = rendicionDao.obtener(id)
    fun historial(rendicionId: Long) = historialDao.deRendicion(rendicionId)
    fun bandejaJefatura(jefaturaId: Long) = rendicionDao.bandejaJefatura(jefaturaId)
    fun porEstado(estado: EstadoRendicion) = rendicionDao.porEstado(estado)

    // ---------- Técnico ----------
    // El ViewModel arma la rendición con numero = "" y aquí se asigna el correlativo
    suspend fun crear(rendicion: Rendicion): Long = db.withTransaction {
        val numero = "RND-%d-%06d".format(LocalDate.now().year, rendicionDao.contar() + 1)
        val id = rendicionDao.insertar(rendicion.copy(numero = numero))
        anotar(id, rendicion.usuarioId, null, EstadoRendicion.BORRADOR.name, "Creada")
        id
    }

    suspend fun actualizar(rendicion: Rendicion) = rendicionDao.actualizar(rendicion)

    suspend fun eliminar(rendicion: Rendicion) = rendicionDao.eliminar(rendicion)

    suspend fun enviar(rendicion: Rendicion, usuarioId: Long, comentario: String? = null) = db.withTransaction {
        gastoDao.marcarEnviados(rendicion.id)
        cambiarEstado(rendicion, EstadoRendicion.ENVIADA, usuarioId, comentario)
    }

    // ---------- Jefatura y Finanzas ----------
    // Finanzas aprueba: los gastos aprobados pasan a APROBADO_FINAL y la rendición queda lista para pagar
    suspend fun aprobarFinanzas(rendicion: Rendicion, usuarioId: Long) = db.withTransaction {
        gastoDao.marcarAprobadoFinal(rendicion.id)
        cambiarEstado(rendicion, EstadoRendicion.APROBADA_FINANZAS, usuarioId, "Aprobada por Finanzas")
    }

    suspend fun cambiarEstado(
        rendicion: Rendicion, nuevo: EstadoRendicion, usuarioId: Long, comentario: String? = null
    ) = db.withTransaction {
        val actualizada = if (nuevo == EstadoRendicion.ENVIADA)
            rendicion.copy(estado = nuevo, fechaEnvio = LocalDateTime.now())
        else
            rendicion.copy(estado = nuevo)
        rendicionDao.actualizar(actualizada)
        anotar(rendicion.id, usuarioId, rendicion.estado.name, nuevo.name, comentario)
    }

    suspend fun registrarPago(pago: Pago, rendiciones: List<Rendicion>) = db.withTransaction {
        val pagoId = pagoDao.insertar(pago)
        rendiciones.forEach {
            rendicionDao.actualizar(it.copy(estado = EstadoRendicion.PAGADA, pagoId = pagoId))
            anotar(it.id, pago.usuarioFinanzasId, it.estado.name, EstadoRendicion.PAGADA.name,
                "Pago ${pago.numeroOperacion}")
        }
    }

    private suspend fun anotar(rendicionId: Long, usuarioId: Long, anterior: String?, nuevo: String, comentario: String?) {
        historialDao.insertar(
            HistorialEstado(rendicionId = rendicionId, usuarioId = usuarioId,
                estadoAnterior = anterior, estadoNuevo = nuevo,
                comentario = comentario, fecha = LocalDateTime.now())
        )
    }
}