package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.GastoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class MisRendicionesViewModel(
    private val session: SessionManager,
    private val rendicionRepo: RendicionRepository,
    private val gastoRepo: GastoRepository
) : ViewModel() {

    // El filtro elegido por el usuario (Todas, Borrador, En aprobación, Observadas)
    private val filtro = MutableStateFlow(FiltroEstado.TODAS)

    @OptIn(ExperimentalCoroutinesApi::class)
    val estado: StateFlow<MisRendicionesUiState> = session.usuarioId
        .filterNotNull()                                    // solo si hay sesión
        .flatMapLatest { usuarioId ->
            // combine: cada vez que cambie CUALQUIERA de los tres, se recalcula el estado
            combine(
                rendicionRepo.misRendiciones(usuarioId),
                gastoRepo.deUsuario(usuarioId),
                filtro
            ) { rendiciones, gastos, filtroActual ->
                val gastosPorRendicion = gastos.groupBy { it.rendicionId }
                val resumenes = rendiciones
                    .filter { filtroActual.incluye(it.estado) }
                    .map { r ->
                        val suyos = gastosPorRendicion[r.id].orEmpty()
                        RendicionResumen(r, cantidadGastos = suyos.size, total = suyos.sumOf { it.monto })
                    }
                MisRendicionesUiState(rendiciones = resumenes, filtro = filtroActual, cargando = false)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MisRendicionesUiState())

    fun onFiltro(nuevo: FiltroEstado) {
        filtro.value = nuevo
    }

    // Qué estados entran en cada filtro
    private fun FiltroEstado.incluye(e: EstadoRendicion): Boolean = when (this) {
        FiltroEstado.TODAS -> true
        FiltroEstado.BORRADOR -> e == EstadoRendicion.BORRADOR
        FiltroEstado.EN_APROBACION -> e == EstadoRendicion.ENVIADA ||
                e == EstadoRendicion.APROBADA_JEFATURA || e == EstadoRendicion.APROBADA_FINANZAS
        FiltroEstado.OBSERVADAS -> e == EstadoRendicion.OBSERVADA
    }
}