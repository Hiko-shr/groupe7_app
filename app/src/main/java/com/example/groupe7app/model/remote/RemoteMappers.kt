package com.example.groupe7app.model.remote

import com.example.groupe7app.model.entity.Categoria
import com.example.groupe7app.model.entity.CentroCosto
import com.example.groupe7app.model.entity.Gasto
import com.example.groupe7app.model.entity.HistorialEstado
import com.example.groupe7app.model.entity.Pago
import com.example.groupe7app.model.entity.Rendicion
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.model.enums.Rol
import java.time.LocalDate
import java.time.LocalDateTime

// Mappers: traducen entre el JSON del backend (DTO) y las entidades de Room.
//   dto.aEntidad()   → lo que llega del servidor se guarda en Room
//   entidad.aRequest() → lo que la app envía al servidor

// ---------- Usuarios ----------
// El servidor nunca envía la clave, así que en Room queda vacía (el login siempre va al servidor)
fun UsuarioDto.aEntidad() = Usuario(
    id = id, nombre = nombre, correo = correo, clave = "",
    rol = Rol.valueOf(rol), jefaturaId = jefaturaId, tieneTarjeta = tieneTarjeta
)

// ---------- Catálogos ----------
fun CategoriaDto.aEntidad() = Categoria(
    id = id, nombre = nombre, topePorGasto = topePorGasto, topeDiario = topeDiario, permiteExceso = permiteExceso
)

fun CentroCostoDto.aEntidad() = CentroCosto(codigo = codigo, nombre = nombre)

// ---------- Rendiciones ----------
fun RendicionDto.aEntidad() = Rendicion(
    id = id, numero = numero, usuarioId = usuarioId, titulo = titulo, tipo = tipo, destino = destino,
    desde = LocalDate.parse(desde), hasta = LocalDate.parse(hasta), centroCosto = centroCosto,
    observacion = observacion, estado = estado,
    fechaEnvio = fechaEnvio?.let { LocalDateTime.parse(it) }, pagoId = pagoId
)

fun Rendicion.aRequest() = RendicionRequest(
    usuarioId = usuarioId, titulo = titulo, tipo = tipo, destino = destino,
    desde = desde.toString(), hasta = hasta.toString(), centroCosto = centroCosto, observacion = observacion
)

fun HistorialDto.aEntidad() = HistorialEstado(
    id = id, rendicionId = rendicionId, gastoId = null, usuarioId = usuarioId,
    estadoAnterior = estadoAnterior, estadoNuevo = estadoNuevo, comentario = comentario,
    fecha = LocalDateTime.parse(fecha)
)

// ---------- Gastos ----------
fun GastoDto.aEntidad() = Gasto(
    id = id, rendicionId = rendicionId, categoriaId = categoriaId,
    fechaDocumento = LocalDate.parse(fechaDocumento), tipoDocumento = tipoDocumento,
    rutEmisor = rutEmisor, folio = folio, proveedor = proveedor, monto = monto, montoAdmitido = montoAdmitido,
    justificacion = justificacion, medioPago = medioPago, centroCosto = centroCosto, descripcion = descripcion,
    ordenTrabajo = ordenTrabajo, rutaFoto = rutaFoto ?: "", latitud = latitud, longitud = longitud,
    estado = estado, montoAprobado = montoAprobado, comentarioRevision = comentarioRevision,
    motivoRechazo = motivoRechazo, respuestaTecnico = respuestaTecnico
)

fun Gasto.aRequest() = GastoRequest(
    rendicionId = rendicionId, categoriaId = categoriaId, fechaDocumento = fechaDocumento.toString(),
    tipoDocumento = tipoDocumento, rutEmisor = rutEmisor, folio = folio, proveedor = proveedor,
    monto = monto, montoAdmitido = montoAdmitido, justificacion = justificacion, medioPago = medioPago,
    centroCosto = centroCosto, descripcion = descripcion, ordenTrabajo = ordenTrabajo,
    rutaFoto = rutaFoto.ifBlank { null }, latitud = latitud, longitud = longitud,
    respuestaTecnico = respuestaTecnico
)

// ---------- Pagos ----------
fun PagoDto.aEntidad() = Pago(
    id = id, fecha = LocalDate.parse(fecha), numeroOperacion = numeroOperacion, monto = monto,
    observacion = observacion, usuarioFinanzasId = usuarioFinanzasId
)

fun Pago.aRequest(rendicionIds: List<Long>) = PagoRequest(
    fecha = fecha.toString(), numeroOperacion = numeroOperacion, monto = monto,
    observacion = observacion, usuarioFinanzasId = usuarioFinanzasId, rendicionIds = rendicionIds
)

// [1, 2, 3] → "1,2,3" (así lo espera @RequestParam List<Long> en Spring)
fun List<Long>.comoLista(): String = joinToString(",")
