package com.example.groupe7app.model.validation

import com.example.groupe7app.model.enums.MotivoRechazo

// Reglas para que la jefatura resuelva un gasto. Devuelven el mensaje de error o null.
object ResolucionValidator {

    // Al aprobar se puede rebajar el monto, pero nunca aprobar más de lo que dice el comprobante
    fun validarMontoAprobado(texto: String, montoComprobante: Int): String? {
        val monto = texto.toIntOrNull()
        return when {
            monto == null || monto <= 0 -> "Ingresa el monto a aprobar"
            monto > montoComprobante -> "No puede superar el monto del comprobante"
            else -> null
        }
    }

    // Al observar, el técnico tiene que saber qué corregir
    fun validarComentarioObservacion(comentario: String): String? =
        if (comentario.trim().length < 10) "Explica qué debe corregir el técnico (mínimo 10 caracteres)" else null

    fun validarMotivo(motivo: MotivoRechazo?): String? =
        if (motivo == null) "Elige el motivo del rechazo" else null

    // Si el motivo es "Otro", el comentario es obligatorio
    fun validarComentarioRechazo(motivo: MotivoRechazo?, comentario: String): String? =
        if (motivo == MotivoRechazo.OTRO && comentario.trim().length < 10)
            "Describe el motivo del rechazo (mínimo 10 caracteres)"
        else null
}
