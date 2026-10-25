package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.entity.Gasto
import com.example.groupe7app.model.entity.Rendicion
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.enums.MedioPago
import com.example.groupe7app.model.repository.GastoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import com.example.groupe7app.model.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Rendiciones aprobadas por la jefatura, esperando la revisión final de Finanzas
class BandejaFinanzasViewModel(
    rendicionRepo: RendicionRepository,
    gastoRepo: GastoRepository,
    usuarioRepo: UsuarioRepository
) : ViewModel() {

    val estado: StateFlow<BandejaFinanzasUiState> =
        rendicionesFinanzas(EstadoRendicion.APROBADA_JEFATURA, rendicionRepo, gastoRepo, usuarioRepo)
            .map { BandejaFinanzasUiState(rendiciones = it, cargando = false) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BandejaFinanzasUiState())
}

// ---------- Funciones compartidas por las pantallas de Finanzas ----------

// Lo aprobado de un gasto: lo que fijó la jefatura (o lo admitido si no lo cambió). Rechazados = 0
fun montoAprobado(gasto: Gasto): Int =
    if (gasto.estado == EstadoGasto.APROBADO || gasto.estado == EstadoGasto.APROBADO_FINAL)
        gasto.montoAprobado ?: gasto.montoAdmitido
    else 0

// Solo se reembolsa lo pagado con dinero propio (el fondo por rendir y la tarjeta ya los pagó la empresa)
fun montoReembolso(gastos: List<Gasto>): Int =
    gastos.filter { it.medioPago == MedioPago.DINERO_PROPIO }.sumOf { montoAprobado(it) }

// Junta rendiciones en un estado + sus gastos + el nombre del técnico
fun rendicionesFinanzas(
    estado: EstadoRendicion,
    rendicionRepo: RendicionRepository,
    gastoRepo: GastoRepository,
    usuarioRepo: UsuarioRepository
): Flow<List<RendicionFinanzas>> = combine(
    rendicionRepo.porEstado(estado),
    gastoRepo.deRendicionesEnEstado(estado),
    usuarioRepo.todos()
) { rendiciones: List<Rendicion>, gastos: List<Gasto>, usuarios: List<Usuario> ->
    val nombres = usuarios.associate { it.id to it.nombre }
    val gastosPorRendicion = gastos.groupBy { it.rendicionId }
    rendiciones.map { r ->
        val suyos = gastosPorRendicion[r.id].orEmpty()
        RendicionFinanzas(
            rendicion = r,
            tecnicoId = r.usuarioId,
            tecnico = nombres[r.usuarioId] ?: "",
            cantidadGastos = suyos.size,
            aprobado = suyos.sumOf { montoAprobado(it) },
            reembolso = montoReembolso(suyos)
        )
    }
}
