package com.example.groupe7app.model.validators

object GastoValidator {

    // Topes máximos definidos por la empresa por categoría
    fun obtenerTopeCategoria(categoria: String): Double {
        return when (categoria.lowercase()) {
            "combustible" -> 80000.0
            "peaje" -> 10000.0
            "estacionamiento" -> 10000.0
            "alimentación", "alimentacion" -> 15000.0
            "alojamiento" -> 60000.0
            "insumos urgentes" -> 100000.0
            "transporte" -> 20000.0
            else -> 20000.0
        }
    }

    // Validar formato de RUT chileno
    fun esRutValido(rut: String): Boolean {
        val cleanRut = rut.replace(".", "").replace("-", "").uppercase()
        if (cleanRut.length < 8) return false
        val dv = cleanRut.last()
        val numStr = cleanRut.dropLast(1)
        return try {
            val num = numStr.toInt()
            var m = 0
            var s = 1
            var t = num
            while (t != 0) {
                s = (s + t % 10 * (9 - m++ % 6)) % 11
                t /= 10
            }
            val expectedDv = if (s != 0) (s + 47).toChar() else 'K'
            dv == expectedDv
        } catch (e: Exception) {
            false
        }
    }

    // Comprobar si el folio ya existe
    fun esFolioDuplicado(folio: String, foliosExistentes: List<String>): Boolean {
        return foliosExistentes.contains(folio.trim())
    }
}