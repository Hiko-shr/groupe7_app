package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.entity.Gasto
import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.enums.MotivoRechazo
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.CatalogoRepository
import com.example.groupe7app.model.repository.GastoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import com.example.groupe7app.model.repository.UsuarioRepository
import com.example.groupe7app.model.validation.ResolucionValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RevisionGastoViewModel(
    private val session: SessionManager,
    private val gastoRepo: GastoRepository,
    private val rendicionRepo: RendicionRepository,
    private val catalogoRepo: CatalogoRepository,
    private val usuarioRepo: UsuarioRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(RevisionGastoUiState())
    val estado: StateFlow<RevisionGastoUiState> = _estado.asStateFlow()

    private var intentoGuardar = false

    fun cargar(gastoId: Long) {
        if (_estado.value.gasto != null) return          // ya cargado
        viewModelScope.launch {
            val gasto = gastoRepo.obtener(gastoId) ?: return@launch
            val categoria = catalogoRepo.categoria(gasto.categoriaId)
            val rendicion = rendicionRepo.obtener(gasto.rendicionId).first()
            val tecnico = rendicion?.let { usuarioRepo.obtener(it.usuarioId).first() }

            // Si ya tenía una decisión (se está corrigiendo), se muestra marcada
            val decisionPrevia = when (gasto.estado) {
                EstadoGasto.APROBADO -> Decision.APROBAR
                EstadoGasto.OBSERVADO -> Decision.OBSERVAR
                EstadoGasto.RECHAZADO -> Decision.RECHAZAR
                else -> null
            }
            _estado.update {
                it.copy(
                    gasto = gasto,
                    categoria = categoria?.nombre ?: "",
                    tecnico = tecnico?.nombre ?: "",
                    decision = decisionPrevia,
                    // Por defecto se propone aprobar lo admitido según el tope
                    montoAprobado = (gasto.montoAprobado ?: gasto.montoAdmitido).toString(),
                    comentario = if (decisionPrevia != null) gasto.comentarioRevision ?: "" else "",
                    motivo = gasto.motivoRechazo,
                    editable = rendicion?.estado == EstadoRendicion.ENVIADA
                )
            }
        }
    }

    fun onDecisionChange(valor: Decision) = actualizar { it.copy(decision = valor) }
    fun onMontoAprobadoChange(valor: String) =
        actualizar { it.copy(montoAprobado = valor.filter { c -> c.isDigit() }.take(9)) }
    fun onComentarioChange(valor: String) = actualizar { it.copy(comentario = valor) }
    fun onMotivoChange(valor: MotivoRechazo) = actualizar { it.copy(motivo = valor) }

    fun onGuardar() {
        intentoGuardar = true
        val actual = _estado.value
        val gasto = actual.gasto ?: return
        val errores = validar(actual)
        _estado.update { it.copy(errores = errores) }
        if (errores.hayErrores()) return

        viewModelScope.launch {
            val jefaturaId = session.usuarioId.first() ?: return@launch
            _estado.update { it.copy(guardando = true) }
            val decision = actual.decision!!               // seguro: el validador revisó que no sea null
            gastoRepo.resolver(
                gasto = gasto,
                nuevo = estadoPara(decision),
                usuarioId = jefaturaId,
                montoAprobado = if (decision == Decision.APROBAR) actual.montoAprobado.toInt() else null,
                comentario = actual.comentario.trim().ifBlank { null },
                motivo = if (decision == Decision.RECHAZAR) actual.motivo else null
            )
            _estado.update { it.copy(guardando = false, guardado = true) }
        }
    }

    private fun estadoPara(decision: Decision) = when (decision) {
        Decision.APROBAR -> EstadoGasto.APROBADO
        Decision.OBSERVAR -> EstadoGasto.OBSERVADO
        Decision.RECHAZAR -> EstadoGasto.RECHAZADO
    }

    private fun actualizar(cambio: (RevisionGastoUiState) -> RevisionGastoUiState) {
        _estado.update { actual ->
            val nuevo = cambio(actual)
            if (intentoGuardar) nuevo.copy(errores = validar(nuevo)) else nuevo
        }
    }

    // Cada decisión tiene sus propios campos obligatorios
    private fun validar(e: RevisionGastoUiState): ResolucionErrores {
        val gasto: Gasto = e.gasto ?: return ResolucionErrores()
        return when (e.decision) {
            null -> ResolucionErrores(decision = "Elige aprobar, observar o rechazar")
            Decision.APROBAR -> ResolucionErrores(
                montoAprobado = ResolucionValidator.validarMontoAprobado(e.montoAprobado, gasto.monto)
            )
            Decision.OBSERVAR -> ResolucionErrores(
                comentario = ResolucionValidator.validarComentarioObservacion(e.comentario)
            )
            Decision.RECHAZAR -> ResolucionErrores(
                motivo = ResolucionValidator.validarMotivo(e.motivo),
                comentario = ResolucionValidator.validarComentarioRechazo(e.motivo, e.comentario)
            )
        }
    }

    private fun ResolucionErrores.hayErrores() =
        listOfNotNull(decision, montoAprobado, comentario, motivo).isNotEmpty()
}
