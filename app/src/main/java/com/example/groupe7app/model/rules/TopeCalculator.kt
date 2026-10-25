package com.example.groupe7app.model.rules

import com.example.groupe7app.model.entity.Categoria

// Regla RN-01: topes por categoría (Apéndice C del SRS).
// - Tope por gasto: se compara el monto con el tope.
// - Tope diario: se compara con lo que queda disponible ese día (tope - lo ya gastado).
// Si la categoría permite exceso, se admite el monto completo pero se exige justificación;
// si no lo permite, solo se admite hasta el tope y el resto no se reembolsa.
object TopeCalculator {

    fun calcular(categoria: Categoria, monto: Int, acumuladoDelDia: Int): ResultadoTope? {
        val porGasto = categoria.topePorGasto
        val diario = categoria.topeDiario

        val (tope, esDiario, disponible) = when {
            porGasto != null -> Triple(porGasto, false, porGasto)
            diario != null -> Triple(diario, true, (diario - acumuladoDelDia).coerceAtLeast(0))
            else -> return null                     // categoría sin tope
        }

        val exceso = (monto - disponible).coerceAtLeast(0)
        val admitido = if (categoria.permiteExceso) monto else monto - exceso

        return ResultadoTope(
            tope = tope,
            esDiario = esDiario,
            disponible = disponible,
            admitido = admitido,
            exceso = exceso,
            permiteExceso = categoria.permiteExceso
        )
    }
}
