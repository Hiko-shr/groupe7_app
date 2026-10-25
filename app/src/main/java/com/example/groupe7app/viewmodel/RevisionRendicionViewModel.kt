package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.CatalogoRepository
import com.example.groupe7app.model.repository.GastoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import com.example.groupe7app.model.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RevisionRendicionViewModel(
    private val session: SessionManager,
    private val rendicionRepo: RendicionRepository,
    gastoRepo: GastoRepository,
    catalogoRepo: CatalogoRepository,
    usuarioRepo: UsuarioRepository
) : ViewModel() {

    private val rendicionId = MutableStateFlow<Long?>(null)

    // Lo que no viene de Room: el diálogo y si ya se finalizó
    private data class Dialogo(val mostrar: Boolean = false, val finalizada: Boolean = false)
    private val dialogo = MutableStateFlow(Dialogo())

    @OptIn(ExperimentalCoroutinesApi::class)
    val estado: StateFlow<RevisionRendicionUiState> = rendicionId
        .filterNotNull()
        .flatMapLatest { id ->
            rendicionRepo.obtener(id).flatMapLatest { rendicion ->
                // El nombre del técnico depende de la rendición, por eso va dentro
                val tecnico: Flow<Usuario?> =
                    if (rendicion == null) flowOf(null) else usuarioRepo.obtener(rendicion.usuarioId)

                combine(gastoRepo.deRendicion(id), catalogoRepo.categorias, tecnico, dialogo) { gastos, categorias, usuario, d ->
                    val nombres = categorias.associate { it.id to it.nombre }

                    // Si hay algún gasto observado, vuelve al técnico; si todos se rechazaron, se rechaza;
                    // en cualquier otro caso pasa a Finanzas
                    val resultado = when {
                        gastos.any { it.estado == EstadoGasto.OBSERVADO } -> EstadoRendicion.OBSERVADA
                        gastos.isNotEmpty() && gastos.all { it.estado == EstadoGasto.RECHAZADO } -> EstadoRendicion.RECHAZADA
                        else -> EstadoRendicion.APROBADA_JEFATURA
                    }

                    RevisionRendicionUiState(
                        rendicion = rendicion,
                        tecnico = usuario?.nombre ?: "",
                        gastos = gastos.map { GastoConCategoria(it, nombres[it.categoriaId] ?: "Sin categoría") },
                        total = gastos.sumOf { it.monto },
                        resueltos = gastos.count { it.estado != EstadoGasto.ENVIADO },
                        // Solo se finaliza cuando todos los gastos tienen decisión
                        puedeFinalizar = rendicion?.estado == EstadoRendicion.ENVIADA &&
                                gastos.isNotEmpty() && gastos.none { it.estado == EstadoGasto.ENVIADO },
                        resultado = resultado,
                        mostrarConfirmacion = d.mostrar,
                        finalizada = d.finalizada
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RevisionRendicionUiState())

    fun cargar(id: Long) {
        rendicionId.value = id
    }

    fun onFinalizarClick() {
        if (estado.value.puedeFinalizar) dialogo.update { it.copy(mostrar = true) }
    }

    fun onCancelar() {
        dialogo.update { it.copy(mostrar = false) }
    }

    fun onConfirmar() {
        val actual = estado.value
        val rendicion = actual.rendicion ?: return
        val resultado = actual.resultado ?: return
        viewModelScope.launch {
            val jefaturaId = session.usuarioId.first() ?: return@launch
            val comentario = when (resultado) {
                EstadoRendicion.OBSERVADA -> "Devuelta al técnico con observaciones"
                EstadoRendicion.RECHAZADA -> "Rechazada por jefatura"
                else -> "Aprobada por jefatura"
            }
            rendicionRepo.cambiarEstado(rendicion, resultado, jefaturaId, comentario)
            dialogo.update { Dialogo(mostrar = false, finalizada = true) }
        }
    }
}
