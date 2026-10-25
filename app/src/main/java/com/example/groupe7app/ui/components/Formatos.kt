package com.example.groupe7app.ui.components

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatoClp(monto: Int): String {
    val formateador = NumberFormat.getNumberInstance(Locale("es", "CL"))
    return "$ ${formateador.format(monto)}"
}

fun formatoFecha(fecha: LocalDate): String {
    val formateador = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    return fecha.format(formateador)
}