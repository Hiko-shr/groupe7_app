package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.GastoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import com.example.groupe7app.model.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BandejaJefaturaViewModel(
    session: SessionManager,
    rendicionRepo: RendicionRepository,
    gastoRepo: GastoRepository,
    usuarioRepo: UsuarioRepository
) : ViewModel() {

    // Rendiciones ENVIADAS por su equipo + los gastos + los nombres de los técnicos
    @OptIn(ExperimentalCoroutinesApi::class)
    val estado: StateFlow<BandejaJefaturaUiState> = session.usuarioId
        .filterNotNull()
        .flatMapLatest { jefaturaId ->
            combine(
                rendicionRepo.bandejaJefatura(jefaturaId),
                gastoRepo.deEquipo(jefaturaId),
                usuarioRepo.deEquipo(jefaturaId)
            ) { rendiciones, gastos, tecnicos ->
                val nombres = tecnicos.associate { it.id to it.nombre }
                val gastosPorRendicion = gastos.groupBy { it.rendicionId }

                BandejaJefaturaUiState(
                    pendientes = rendiciones.map { r ->
                        val suyos = gastosPorRendicion[r.id].orEmpty()
                        RendicionPendiente(
                            rendicion = r,
                            tecnico = nombres[r.usuarioId] ?: "",
                            cantidadGastos = suyos.size,
                            total = suyos.sumOf { it.monto },
                            // Alerta = gasto que supera el tope (tiene exceso o justificación)
                            alertas = suyos.count { it.monto > it.montoAdmitido || it.justificacion != null },
                            diasEspera = r.fechaEnvio
                                ?.let { ChronoUnit.DAYS.between(it.toLocalDate(), LocalDate.now()).toInt() } ?: 0
                        )
                    },
                    cargando = false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BandejaJefaturaUiState())
}
