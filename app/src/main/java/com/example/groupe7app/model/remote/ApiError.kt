package com.example.groupe7app.model.remote

import com.google.gson.JsonParser
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// Convierte cualquier error de red en un mensaje para mostrar en pantalla.
// El backend responde los errores como {"status": 409, "message": "Este comprobante ya está rendido en RND-..."}
fun Throwable.mensajeApi(): String = when (this) {
    is HttpException -> {
        val cuerpo = response()?.errorBody()?.string()
        val mensaje = runCatching { JsonParser.parseString(cuerpo).asJsonObject.get("message")?.asString }.getOrNull()
        if (!mensaje.isNullOrBlank()) mensaje else "Error del servidor (${code()})"
    }
    is IOException -> "Sin conexión con el servidor. Revisa que los microservicios estén encendidos."
    else -> message ?: "Error inesperado"
}

// Código HTTP del error (401, 409...) o null si no fue un error HTTP
fun Throwable.codigoHttp(): Int? = (this as? HttpException)?.code()

// Para los DELETE, que responden 204 sin cuerpo: si no fue 2xx, se lanza el error como en los demás
fun Response<Unit>.exigirExito() {
    if (!isSuccessful) throw HttpException(this)
}
