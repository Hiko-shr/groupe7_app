package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.CatalogoRepository
import com.example.groupe7app.model.repository.GastoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import com.example.groupe7app.model.repository.UsuarioRepository
import com.example.groupe7app.model.validation.FinanzasValidator
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

// Revisión final de Finanzas: aprueba la rendición completa (queda lista para pagar) o la rechaza
class RevisionFinanzasViewModel(
    private val session: SessionManager,
    private val rendicionRepo: RendicionRepository,
    gastoRepo: GastoRepository,
    catalogoRepo: CatalogoRepository,
    usuarioRepo: UsuarioRepository
) : ViewModel() {

    private val rendicionId = MutableStateFlow<Long?>(null)

    // Lo que no viene de Room: qué diálogo está abierto, el comentario de rechazo y si ya terminó
    private data class Formulario(
        val dialogo: DialogoFinanzas? = null,
        val comentario: String = "",
        val error: String? = null,
        val finalizada: Boolean = false
    )
    private val formulario = MutableStateFlow(Formulario())

    @OptIn(ExperimentalCoroutinesApi::class)
    val estado: StateFlow<RevisionFinanzasUiState> = rendicionId
        .filterNotNull()
        .flatMapLatest { id ->
            rendicionRepo.obtener(id).flatMapLatest { rendicion ->
                val tecnico: Flow<Usuario?> =
                    if (rendicion == null) flowOf(null) else usuarioRepo.obtener(rendicion.usuarioId)

                combine(gastoRepo.deRendicion(id), catalogoRepo.categorias, tecnico, formulario) { gastos, categorias, usuario, f ->
                    val nombres = categorias.associate { it.id to it.nombre }
                    RevisionFinanzasUiState(
                        rendicion = rendicion,
                        tecnico = usuario?.nombre ?: "",
                        gastos = gastos.map { GastoConCategoria(it, nombres[it.categoriaId] ?: "Sin categoría") },
                        aprobado = gastos.sumOf { montoAprobado(it) },
                        reembolso = montoReembolso(gastos),
                        editable = rendicion?.estado == EstadoRendicion.APROBADA_JEFATURA,
                        dialogo = f.dialogo,
                        comentario = f.comentario,
                        errorComentario = f.error,
                        finalizada = f.finalizada
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RevisionFinanzasUiState())

    fun cargar(id: Long) {
        rendicionId.value = id
    }

    fun onAbrirDialogo(dialogo: DialogoFinanzas) = formulario.update { it.copy(dialogo = dialogo, error = null) }
    fun onCerrarDialogo() = formulario.update { it.copy(dialogo = null) }
    fun onComentarioChange(valor: String) = formulario.update { it.copy(comentario = valor, error = null) }

    fun onConfirmar() {
        val actual = estado.value
        val rendicion = actual.rendicion ?: return
        val dialogo = actual.dialogo ?: return

        // Para rechazar se exige un comentario
        if (dialogo == DialogoFinanzas.RECHAZAR) {
            val error = FinanzasValidator.validarComentarioRechazo(actual.comentario)
            if (error != null) {
                formulario.update { it.copy(error = error) }
                return
            }
        }

        viewModelScope.launch {
            val usuarioId = session.usuarioId.first() ?: return@launch
            when (dialogo) {
                DialogoFinanzas.APROBAR -> rendicionRepo.aprobarFinanzas(rendicion, usuarioId)
                DialogoFinanzas.RECHAZAR -> rendicionRepo.cambiarEstado(
                    rendicion, EstadoRendicion.RECHAZADA, usuarioId, actual.comentario.trim()
                )
            }
            formulario.update { it.copy(dialogo = null, finalizada = true) }
        }
    }
}
