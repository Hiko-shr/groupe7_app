package com.example.groupe7app.model.validation

// Reglas de Finanzas: rechazo de una rendición y registro de un pago
object FinanzasValidator {

    fun validarComentarioRechazo(comentario: String): String? =
        if (comentario.trim().length < 10) "Explica el motivo del rechazo (mínimo 10 caracteres)" else null

    // Un pago es UNA transferencia a UN técnico: se pueden juntar varias rendiciones, pero del mismo técnico
    fun validarSeleccion(cantidad: Int, tecnicosDistintos: Int): String? = when {
        cantidad == 0 -> "Selecciona al menos una rendición"
        tecnicosDistintos > 1 -> "Solo puedes pagar juntas rendiciones del mismo técnico"
        else -> null
    }

    // N.º de operación de la transferencia bancaria: letras, números y guiones
    fun validarNumeroOperacion(numero: String): String? {
        val limpio = numero.trim()
        return when {
            limpio.isEmpty() -> "Ingresa el número de operación de la transferencia"
            limpio.length !in 4..20 -> "Debe tener entre 4 y 20 caracteres"
            !limpio.all { it.isLetterOrDigit() || it == '-' } -> "Solo letras, números y guiones"
            else -> null
        }
    }

    fun validarObservacion(observacion: String): String? =
        if (observacion.length > 200) "Máximo 200 caracteres" else null
}
