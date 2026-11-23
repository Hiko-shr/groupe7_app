package com.example.groupe7app.model.remote

import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.enums.MedioPago
import com.example.groupe7app.model.enums.MotivoRechazo
import com.example.groupe7app.model.enums.TipoDocumento
import com.example.groupe7app.model.enums.TipoRendicion

// DTOs = el JSON que va y viene de los microservicios, campo por campo.
// Las fechas llegan como texto ("2026-11-16"); los mappers las convierten a LocalDate.
// Los enums viajan por su nombre ("ENVIADA"), así que deben llamarse igual que en el backend.

// ---------- ms-usuarios ----------
data class UsuarioDto(
    val id: Long = 0,
    val nombre: String = "",
    val correo: String = "",
    val rol: String = "",                 // "TECNICO" | "JEFATURA" | "FINANZAS"
    val jefaturaId: Long? = null,
    val tieneTarjeta: Boolean = false
)

data class LoginRequest(val correo: String, val clave: String)

// ---------- ms-rendiciones: catálogos ----------
data class CategoriaDto(
    val id: Long = 0,
    val nombre: String = "",
    val topePorGasto: Int? = null,
    val topeDiario: Int? = null,
    val permiteExceso: Boolean = false
)

data class CentroCostoDto(val codigo: String = "", val nombre: String = "")

// ---------- ms-rendiciones: rendiciones ----------
data class RendicionDto(
    val id: Long = 0,
    val numero: String = "",
    val usuarioId: Long = 0,
    val titulo: String = "",
    val tipo: TipoRendicion = TipoRendicion.GASTOS_GENERALES,
    val destino: String? = null,
    val desde: String = "",
    val hasta: String = "",
    val centroCosto: String = "",
    val observacion: String? = null,
    val estado: EstadoRendicion = EstadoRendicion.BORRADOR,
    val fechaEnvio: String? = null,       // "2026-11-22T23:10:05"
    val pagoId: Long? = null
)

data class RendicionRequest(
    val usuarioId: Long,
    val titulo: String,
    val tipo: TipoRendicion,
    val destino: String?,
    val desde: String,
    val hasta: String,
    val centroCosto: String,
    val observacion: String?
)

data class AccionRequest(val usuarioId: Long, val comentario: String? = null)

data class CambioEstadoRequest(val estado: EstadoRendicion, val usuarioId: Long, val comentario: String?)

data class HistorialDto(
    val id: Long = 0,
    val rendicionId: Long = 0,
    val estadoAnterior: String? = null,
    val estadoNuevo: String = "",
    val usuarioId: Long = 0,
    val comentario: String? = null,
    val fecha: String = ""
)

// ---------- ms-rendiciones: gastos ----------
data class GastoDto(
    val id: Long = 0,
    val rendicionId: Long = 0,
    val categoriaId: Long = 0,
    val fechaDocumento: String = "",
    val tipoDocumento: TipoDocumento = TipoDocumento.BOLETA,
    val rutEmisor: String? = null,
    val folio: String? = null,
    val proveedor: String = "",
    val monto: Int = 0,
    val montoAdmitido: Int = 0,
    val justificacion: String? = null,
    val medioPago: MedioPago = MedioPago.DINERO_PROPIO,
    val centroCosto: String = "",
    val descripcion: String = "",
    val ordenTrabajo: String? = null,
    val rutaFoto: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val estado: EstadoGasto = EstadoGasto.BORRADOR,
    val montoAprobado: Int? = null,
    val comentarioRevision: String? = null,
    val motivoRechazo: MotivoRechazo? = null,
    val respuestaTecnico: String? = null
)

data class GastoRequest(
    val rendicionId: Long,
    val categoriaId: Long,
    val fechaDocumento: String,
    val tipoDocumento: TipoDocumento,
    val rutEmisor: String?,
    val folio: String?,
    val proveedor: String,
    val monto: Int,
    val montoAdmitido: Int,
    val justificacion: String?,
    val medioPago: MedioPago,
    val centroCosto: String,
    val descripcion: String,
    val ordenTrabajo: String?,
    val rutaFoto: String?,
    val latitud: Double?,
    val longitud: Double?,
    val respuestaTecnico: String?
)

data class ResolucionRequest(
    val estado: EstadoGasto,
    val usuarioId: Long,
    val montoAprobado: Int?,
    val comentario: String?,
    val motivo: MotivoRechazo?
)

data class DuplicadoDto(val rendicion: String? = null)       // null = no está duplicado

data class AcumuladoDto(val total: Long = 0)

// ---------- ms-rendiciones: pagos ----------
data class PagoDto(
    val id: Long = 0,
    val fecha: String = "",
    val numeroOperacion: String = "",
    val monto: Int = 0,
    val observacion: String? = null,
    val usuarioFinanzasId: Long = 0
)

data class PagoRequest(
    val fecha: String?,
    val numeroOperacion: String,
    val monto: Int,
    val observacion: String?,
    val usuarioFinanzasId: Long,
    val rendicionIds: List<Long>
)
