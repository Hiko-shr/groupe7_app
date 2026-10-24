package com.example.groupe7app.model.validation

// Kotlin puro: no sabe nada de pantallas. Devuelve el mensaje de error, o null si está bien.
object LoginValidator {

    private val FORMATO_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validarCorreo(correo: String): String? = when {
        correo.isBlank() -> "Ingresa tu correo"
        !FORMATO_CORREO.matches(correo.trim()) -> "Ingresa un correo válido, por ejemplo nombre@teqmed.cl"
        else -> null
    }

    fun validarClave(clave: String): String? = when {
        clave.isEmpty() -> "Ingresa tu contraseña"
        clave.length < 8 -> "Mínimo 8 caracteres"
        else -> null
    }
}