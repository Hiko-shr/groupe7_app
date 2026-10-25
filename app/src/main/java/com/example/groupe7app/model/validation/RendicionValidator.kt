package com.example.groupe7app.model.validation

import com.example.groupe7app.model.enums.TipoRendicion
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// Reglas del formulario de rendición (SRS 3.5.2). Cada función devuelve el mensaje de error o null.
object RendicionValidator {

    fun validarTitulo(titulo: String): String? = when {
        titulo.isBlank() -> "Ingresa un título"
        titulo.trim().length !in 5..80 -> "El título debe tener entre 5 y 80 caracteres"
        else -> null
    }

    // El destino solo es obligatorio en una comisión de servicio
    fun validarDestino(tipo: TipoRendicion, destino: String): String? =
        if (tipo == TipoRendicion.COMISION_SERVICIO && destino.trim().length < 3)
            "Indica el destino de la comisión"
        else null

    fun validarPeriodo(desde: LocalDate?, hasta: LocalDate?): String? = when {
        desde == null || hasta == null -> "Elige la fecha de inicio y la de término"
        hasta.isBefore(desde) -> "La fecha de término no puede ser anterior al inicio"
        ChronoUnit.DAYS.between(desde, hasta) > 31 -> "El período no puede superar 31 días"
        else -> null
    }

    fun validarCentroCosto(codigo: String?): String? =
        if (codigo.isNullOrBlank()) "Elige un centro de costo" else null

    fun validarObservacion(observacion: String): String? =
        if (observacion.length > 200) "Máximo 200 caracteres" else null
}
