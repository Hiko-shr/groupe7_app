package com.example.groupe7app.model.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

// La idea central de la integración:
//   1) La pantalla recibe AL TIRO lo que hay guardado en Room (funciona aunque no haya señal).
//   2) Al mismo tiempo se piden los datos frescos al microservicio y se guardan en Room.
//   3) Como Room devuelve Flow, la pantalla se actualiza sola con los datos nuevos.
// Si el servidor no responde, la pantalla se queda con lo que había en Room.
fun <T> Flow<T>.refrescandoCon(sincronizar: suspend () -> Unit): Flow<T> = channelFlow {
    launch { intentar { sincronizar() } }
    collect { send(it) }
}

// Ejecuta algo que puede fallar por la red sin botar la app (pero sin tragarse una cancelación)
suspend fun intentar(accion: suspend () -> Unit) {
    try {
        accion()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // sin conexión o error del servidor: se mantiene lo que había en Room
    }
}
