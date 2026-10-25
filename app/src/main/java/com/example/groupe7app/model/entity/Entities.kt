package com.example.groupe7app.model.entity

import com.example.groupe7app.model.enums.*
import java.time.LocalDate
import java.time.LocalDateTime

data class Usuario(val id: Long = 0, val nombre: String, val correo: String, val clave: String, val rol: Rol, val jefaturaId: Long? = null, val tieneTarjeta: Boolean = false)
data class Categoria(val id: Long = 0, val nombre: String, val topePorGasto: Int? = null, val topeDiario: Int? = null, val permiteExceso: Boolean)
data class CentroCosto(val codigo: String, val nombre: String)
data class Rendicion(val id: Long = 0, val numero: String, val usuarioId: Long, val titulo: String, val tipo: TipoRendicion, val destino: String? = null, val desde: LocalDate, val hasta: LocalDate, val centroCosto: String, val observacion: String? = null, val estado: EstadoRendicion = EstadoRendicion.BORRADOR, val fechaEnvio: LocalDateTime? = null, val pagoId: Long? = null)
data class Gasto(val id: Long = 0, val rendicionId: Long, val categoriaId: Long, val fechaDocumento: LocalDate, val tipoDocumento: TipoDocumento, val rutEmisor: String? = null, val folio: String? = null, val proveedor: String, val monto: Int, val montoAdmitido: Int, val justificacion: String? = null, val medioPago: MedioPago, val centroCosto: String, val descripcion: String, val ordenTrabajo: String? = null, val rutaFoto: String, val latitud: Double? = null, val longitud: Double? = null, val estado: EstadoGasto = EstadoGasto.BORRADOR, val montoAprobado: Int? = null, val comentarioRevision: String? = null, val motivoRechazo: MotivoRechazo? = null, val respuestaTecnico: String? = null)
data class HistorialEstado(val id: Long = 0, val rendicionId: Long, val gastoId: Long? = null, val usuarioId: Long, val estadoAnterior: String?, val estadoNuevo: String, val comentario: String? = null, val fecha: LocalDateTime)