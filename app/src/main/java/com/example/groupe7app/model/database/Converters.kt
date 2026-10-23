package com.example.groupe7app.model.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun fechaATexto(fecha: LocalDate?): String? = fecha?.toString()

    @TypeConverter
    fun textoAFecha(texto: String?): LocalDate? = texto?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fechaHoraATexto(fechaHora: LocalDateTime?): String? = fechaHora?.toString()

    @TypeConverter
    fun textoAFechaHora(texto: String?): LocalDateTime? = texto?.let { LocalDateTime.parse(it) }
}