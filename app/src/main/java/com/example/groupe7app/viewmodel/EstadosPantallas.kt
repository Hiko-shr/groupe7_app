package com.example.groupe7app.viewmodel

import com.example.groupe7app.model.entity.*
import com.example.groupe7app.model.enums.*
import com.example.groupe7app.model.rules.ResultadoTope
import java.time.LocalDate

// Clases de apoyo para mostrar
data class RendicionResumen(val rendicion: Rendicion, val cantidadGastos: Int, val total: Int)
data class GastoConCategoria(val gasto: Gasto, val categoria: String)

// ---------- Técnico ----------
data class ResumenMes(val enBorrador: Int = 0, val enAprobacion: Int = 0, val porReembolsar: Int = 0, val reembolsado: Int = 0)

data class InicioTecnicoUiState(
    val nombre: String = "",
    val resumen: ResumenMes = ResumenMes(),
    val observadas: List<RendicionResumen> = emptyList(),
    val enCurso: RendicionResumen? = null,
    val cargando: Boolean = true
)

enum class FiltroEstado(val etiqueta: String) { TODAS("Todas"), BORRADOR("Borrador"), EN_APROBACION("En aprobación"), OBSERVADAS("Observadas") }

data class MisRendicionesUiState(
    val rendiciones: List<RendicionResumen> = emptyList(),
    val filtro: FiltroEstado = FiltroEstado.TODAS,
    val cargando: Boolean = true
)