package com.example.groupe7app.model.validation

import com.example.groupe7app.model.entity.Categoria
import com.example.groupe7app.model.enums.MedioPago
import com.example.groupe7app.model.enums.TipoDocumento
import com.example.groupe7app.model.rules.ResultadoTope
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// Reglas del formulario de gasto. Cada función devuelve el mensaje de error o null.
// (Reemplaza al GastoValidator antiguo de model/validators)
object GastoValidator {

    fun validarCategoria(categoria: Categoria?): String? =
        if (categoria == null) "Elige una categoría" else null

    fun validarFoto(ruta: String?): String? =
        if (ruta.isNullOrBlank()) "Agrega la foto del comprobante" else null

    fun validarFecha(fecha: LocalDate, hoy: LocalDate = LocalDate.now()): String? = when {
        fecha.isAfter(hoy) -> "La fecha no puede ser futura"
        ChronoUnit.DAYS.between(fecha, hoy) > 30 -> "El comprobante tiene más de 30 días (plazo de rendición)"
        else -> null
    }

    // Los tickets (peaje, estacionamiento) no traen RUT ni folio
    fun requiereRut(tipo: TipoDocumento): Boolean = tipo != TipoDocumento.TICKET

    fun validarRut(tipo: TipoDocumento, rut: String): String? = when {
        !requiereRut(tipo) -> null
        rut.isBlank() -> "Ingresa el RUT del emisor"
        !esRutValido(rut) -> "RUT inválido, revisa el dígito verificador"
        else -> null
    }

    fun validarFolio(tipo: TipoDocumento, folio: String): String? = when {
        !requiereRut(tipo) -> null
        folio.isBlank() -> "Ingresa el folio del documento"
        !folio.trim().all { it.isDigit() } || folio.trim().length > 10 -> "El folio solo lleva números (máx. 10)"
        else -> null
    }

    fun validarProveedor(proveedor: String): String? =
        if (proveedor.trim().length !in 2..80) "Ingresa el nombre del comercio" else null

    fun validarMonto(monto: String): String? {
        val valor = monto.toIntOrNull()
        return when {
            monto.isBlank() -> "Ingresa el monto"
            valor == null || valor <= 0 -> "El monto debe ser mayor a 0"
            valor > 5_000_000 -> "El monto no puede superar $ 5.000.000"
            else -> null
        }
    }

    fun validarMedioPago(medio: MedioPago?, tieneTarjeta: Boolean): String? = when {
        medio == null -> "Elige el medio de pago"
        medio == MedioPago.TARJETA_CORPORATIVA && !tieneTarjeta -> "No tienes tarjeta corporativa asignada"
        else -> null
    }

    fun validarCentroCosto(codigo: String?): String? =
        if (codigo.isNullOrBlank()) "Elige un centro de costo" else null

    fun validarDescripcion(descripcion: String): String? =
        if (descripcion.trim().length !in 5..200) "Describe el gasto (entre 5 y 200 caracteres)" else null

    // Solo se pide si el gasto supera el tope y la categoría permite exceso
    fun validarJustificacion(tope: ResultadoTope?, justificacion: String): String? =
        if (tope?.requiereJustificacion == true && justificacion.trim().length < 10)
            "Explica por qué se superó el tope (mínimo 10 caracteres)"
        else null

    // Si la jefatura observó el gasto, el técnico debe responder
    fun validarRespuesta(observado: Boolean, respuesta: String): String? =
        if (observado && respuesta.trim().length < 5) "Responde la observación de la jefatura" else null

    // ---------- RUT chileno (módulo 11) ----------
    fun esRutValido(rut: String): Boolean {
        val limpio = limpiarRut(rut)
        if (limpio.length !in 8..9) return false
        val cuerpo = limpio.dropLast(1)
        val dv = limpio.last()
        if (!cuerpo.all { it.isDigit() }) return false

        // Se multiplica cada dígito (de derecha a izquierda) por 2, 3, 4, 5, 6, 7, 2, 3...
        var suma = 0
        var factor = 2
        for (digito in cuerpo.reversed()) {
            suma += (digito - '0') * factor
            factor = if (factor == 7) 2 else factor + 1
        }
        val esperado = when (val resto = 11 - suma % 11) {
            11 -> '0'
            10 -> 'K'
            else -> '0' + resto
        }
        return dv == esperado
    }

    // "76.543.210-3" → "76543210-3" (así se guarda y se compara para detectar duplicados)
    fun normalizarRut(rut: String): String {
        val limpio = limpiarRut(rut)
        return limpio.dropLast(1) + "-" + limpio.last()
    }

    private fun limpiarRut(rut: String) = rut.replace(".", "").replace("-", "").trim().uppercase()
}
