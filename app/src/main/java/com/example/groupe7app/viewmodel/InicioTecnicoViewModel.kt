package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.EstadoRendicion
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

class InicioTecnicoViewModel(
    session: SessionManager,
    usuarioRepo: UsuarioRepository,
    rendicionRepo: RendicionRepository,
    gastoRepo: GastoRepository
) : ViewModel() {

    // Igual que Mis rendiciones: usuario + rendiciones + gastos, combinados en un solo estado
    @OptIn(ExperimentalCoroutinesApi::class)
    val estado: StateFlow<InicioTecnicoUiState> = session.usuarioId
        .filterNotNull()
        .flatMapLatest { usuarioId ->
            combine(
                usuarioRepo.obtener(usuarioId),
                rendicionRepo.misRendiciones(usuarioId),
                gastoRepo.deUsuario(usuarioId)
            ) { usuario, rendiciones, gastos ->
                val gastosPorRendicion = gastos.groupBy { it.rendicionId }
                val resumenes = rendiciones.map { r ->
                    val suyos = gastosPorRendicion[r.id].orEmpty()
                    RendicionResumen(r, cantidadGastos = suyos.size, total = suyos.sumOf { it.monto })
                }

                // Suma los totales de las rendiciones que están en alguno de esos estados
                fun sumar(vararg estados: EstadoRendicion) =
                    resumenes.filter { it.rendicion.estado in estados }.sumOf { it.total }

                InicioTecnicoUiState(
                    nombre = usuario?.nombre ?: "",
                    resumen = ResumenMes(
                        enBorrador = sumar(EstadoRendicion.BORRADOR),
                        enAprobacion = sumar(EstadoRendicion.ENVIADA, EstadoRendicion.APROBADA_JEFATURA),
                        porReembolsar = sumar(EstadoRendicion.APROBADA_FINANZAS),
                        reembolsado = sumar(EstadoRendicion.PAGADA)
                    ),
                    // En el aviso se cuentan solo los gastos que la jefatura observó
                    observadas = resumenes
                        .filter { it.rendicion.estado == EstadoRendicion.OBSERVADA }
                        .map { res ->
                            res.copy(cantidadGastos = gastosPorRendicion[res.rendicion.id].orEmpty()
                                .count { it.estado == EstadoGasto.OBSERVADO })
                        },
                    // La lista viene ordenada de la más nueva a la más antigua: el primer borrador es el actual
                    enCurso = resumenes.firstOrNull { it.rendicion.estado == EstadoRendicion.BORRADOR },
                    cargando = false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InicioTecnicoUiState())
}
