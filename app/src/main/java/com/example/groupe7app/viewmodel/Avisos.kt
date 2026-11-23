package com.example.groupe7app.viewmodel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// Mensajes cortos para toda la app (por ejemplo "Sin conexión con el servidor").
// Cualquier ViewModel publica con Avisos.mostrar(...) y AppNavigation los muestra en un Snackbar.
// Es un SharedFlow y no un StateFlow: cada mensaje se muestra una vez y no se repite al girar la pantalla.
object Avisos {
    private val _mensajes = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val mensajes: SharedFlow<String> = _mensajes.asSharedFlow()

    fun mostrar(mensaje: String) {
        _mensajes.tryEmit(mensaje)
    }
}
