package com.example.groupe7app.model.rules

data class ResultadoTope(
    val tope: Int?,
    val esDiario: Boolean,
    val disponible: Int?,
    val admitido: Int,
    val exceso: Int,
    val permiteExceso: Boolean
) {
    val superaTope: Boolean get() = exceso > 0
    val requiereJustificacion: Boolean get() = superaTope && permiteExceso
}