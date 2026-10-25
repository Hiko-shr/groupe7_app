package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.entity.Pago
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.GastoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import com.example.groupe7app.model.repository.UsuarioRepository
import com.example.groupe7app.model.validation.FinanzasValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// Registrar el pago (transferencia) de una o más rendiciones aprobadas de un mismo técnico
class PagoViewModel(
    private val session: SessionManager,
    private val rendicionRepo: RendicionRepository,
    gastoRepo: GastoRepository,
    usuarioRepo: UsuarioRepository
) : ViewModel() {

    // Lo que escribe/elige el usuario (no viene de Room)
    private data class Formulario(
        val seleccionadas: Set<Long> = emptySet(),
        val numeroOperacion: String = "",
        val observacion: String = "",
        val errores: PagoErrores = PagoErrores(),
        val guardando: Boolean = false,
        val mensaje: String? = null
    )
    private val formulario = MutableStateFlow(Formulario())
    private var intentoGuardar = false

    // Rendiciones listas para pagar (de Room) + el formulario → un solo estado
    val estado: StateFlow<PagoUiState> = combine(
        rendicionesFinanzas(EstadoRendicion.APROBADA_FINANZAS, rendicionRepo, gastoRepo, usuarioRepo),
        formulario
    ) { pendientes, f ->
        PagoUiState(
            pendientes = pendientes,
            seleccionadas = f.seleccionadas,
            numeroOperacion = f.numeroOperacion,
            observacion = f.observacion,
            total = pendientes.filter { it.rendicion.id in f.seleccionadas }.sumOf { it.reembolso },
            errores = f.errores,
            guardando = f.guardando,
            mensaje = f.mensaje,
            cargando = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PagoUiState())

    // Marca o desmarca una rendición
    fun onSeleccionar(rendicionId: Long) = actualizar { f ->
        val nuevas = if (rendicionId in f.seleccionadas) f.seleccionadas - rendicionId else f.seleccionadas + rendicionId
        f.copy(seleccionadas = nuevas, mensaje = null)
    }

    fun onNumeroOperacionChange(valor: String) = actualizar { it.copy(numeroOperacion = valor.uppercase().take(20)) }
    fun onObservacionChange(valor: String) = actualizar { it.copy(observacion = valor) }

    fun onRegistrar() {
        intentoGuardar = true
        val actual = estado.value
        val errores = validar(formulario.value)
        formulario.update { it.copy(errores = errores) }
        if (listOfNotNull(errores.seleccion, errores.numeroOperacion, errores.observacion).isNotEmpty()) return

        val rendiciones = actual.pendientes.filter { it.rendicion.id in actual.seleccionadas }.map { it.rendicion }
        viewModelScope.launch {
            val usuarioId = session.usuarioId.first() ?: return@launch
            formulario.update { it.copy(guardando = true) }
            val numero = actual.numeroOperacion.trim()
            rendicionRepo.registrarPago(
                Pago(
                    fecha = LocalDate.now(),
                    numeroOperacion = numero,
                    monto = actual.total,
                    observacion = actual.observacion.trim().ifBlank { null },
                    usuarioFinanzasId = usuarioId
                ),
                rendiciones
            )
            // Formulario limpio y mensaje de confirmación; las rendiciones pagadas salen solas de la lista
            intentoGuardar = false
            formulario.value = Formulario(
                mensaje = "Pago $numero registrado: ${rendiciones.size} rendición(es) quedaron pagadas"
            )
        }
    }

    private fun actualizar(cambio: (Formulario) -> Formulario) {
        formulario.update { actual ->
            val nuevo = cambio(actual)
            if (intentoGuardar) nuevo.copy(errores = validar(nuevo)) else nuevo
        }
    }

    private fun validar(f: Formulario): PagoErrores {
        val elegidas = estado.value.pendientes.filter { it.rendicion.id in f.seleccionadas }
        return PagoErrores(
            seleccion = FinanzasValidator.validarSeleccion(elegidas.size, elegidas.map { it.tecnicoId }.distinct().size),
            numeroOperacion = FinanzasValidator.validarNumeroOperacion(f.numeroOperacion),
            observacion = FinanzasValidator.validarObservacion(f.observacion)
        )
    }
}
