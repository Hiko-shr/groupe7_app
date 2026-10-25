package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.enums.MedioPago
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.CatalogoRepository
import com.example.groupe7app.model.repository.GastoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetalleRendicionViewModel(
    private val session: SessionManager,
    private val rendicionRepo: RendicionRepository,
    gastoRepo: GastoRepository,
    catalogoRepo: CatalogoRepository
) : ViewModel() {

    // Qué rendición mostrar: la pantalla lo indica con cargar(id)
    private val rendicionId = MutableStateFlow<Long?>(null)

    // Lo que no viene de Room: el diálogo de confirmación y el resultado del envío
    private data class Envio(
        val mostrarConfirmacion: Boolean = false,
        val error: String? = null,
        val enviada: Boolean = false
    )
    private val envio = MutableStateFlow(Envio())

    // Junta 5 fuentes (rendición, gastos, categorías, historial y envío) en un solo estado.
    // Si cualquiera cambia en la base de datos, la pantalla se actualiza sola.
    @OptIn(ExperimentalCoroutinesApi::class)
    val estado: StateFlow<DetalleRendicionUiState> = rendicionId
        .filterNotNull()
        .flatMapLatest { id ->
            combine(
                rendicionRepo.obtener(id),
                gastoRepo.deRendicion(id),
                catalogoRepo.categorias,
                rendicionRepo.historial(id),
                envio
            ) { rendicion, gastos, categorias, historial, envioActual ->
                val nombres = categorias.associate { it.id to it.nombre }
                DetalleRendicionUiState(
                    rendicion = rendicion,
                    gastos = gastos.map { GastoConCategoria(it, nombres[it.categoriaId] ?: "Sin categoría") },
                    totalesPorMedio = gastos.groupBy { it.medioPago }.mapValues { (_, lista) -> lista.sumOf { it.monto } },
                    total = gastos.sumOf { it.monto },
                    exceso = gastos.sumOf { it.monto - it.montoAdmitido },
                    // Solo se reembolsa lo pagado con dinero propio, hasta el tope
                    reembolsoEstimado = gastos.filter { it.medioPago == MedioPago.DINERO_PROPIO }.sumOf { it.montoAdmitido },
                    historial = historial,
                    editable = rendicion?.estado == EstadoRendicion.BORRADOR ||
                            rendicion?.estado == EstadoRendicion.OBSERVADA,
                    mostrarConfirmacion = envioActual.mostrarConfirmacion,
                    errorEnvio = envioActual.error,
                    enviada = envioActual.enviada
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleRendicionUiState())

    fun cargar(id: Long) {
        rendicionId.value = id
    }

    // Antes de abrir la confirmación se revisan las reglas de envío
    fun onEnviarClick() {
        val actual = estado.value
        val error = when {
            actual.gastos.isEmpty() ->
                "Agrega al menos un gasto antes de enviar"
            actual.gastos.any { it.gasto.estado == EstadoGasto.OBSERVADO && it.gasto.respuestaTecnico.isNullOrBlank() } ->
                "Responde las observaciones de la jefatura antes de reenviar"
            else -> null
        }
        envio.update { it.copy(error = error, mostrarConfirmacion = error == null) }
    }

    fun onCancelarEnvio() {
        envio.update { it.copy(mostrarConfirmacion = false) }
    }

    fun onConfirmarEnvio() {
        val rendicion = estado.value.rendicion ?: return
        viewModelScope.launch {
            val usuarioId = session.usuarioId.first() ?: return@launch
            rendicionRepo.enviar(rendicion, usuarioId)      // gastos → ENVIADO, rendición → ENVIADA + historial
            envio.update { it.copy(mostrarConfirmacion = false, enviada = true) }
        }
    }
}
