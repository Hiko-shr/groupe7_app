package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.entity.Rendicion
import com.example.groupe7app.model.enums.TipoRendicion
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.CatalogoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import com.example.groupe7app.model.validation.RendicionValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class NuevaRendicionViewModel(
    private val session: SessionManager,
    private val rendicionRepo: RendicionRepository,
    catalogoRepo: CatalogoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(NuevaRendicionUiState())
    val estado: StateFlow<NuevaRendicionUiState> = _estado.asStateFlow()

    // Después del primer intento de guardar, cada cambio se vuelve a validar
    private var intentoGuardar = false

    init {
        // Carga los centros de costo desde Room para el desplegable
        viewModelScope.launch {
            catalogoRepo.centrosCosto.collect { lista -> _estado.update { it.copy(centros = lista) } }
        }
    }

    fun onTituloChange(valor: String) = actualizar { it.copy(titulo = valor) }
    fun onTipoChange(valor: TipoRendicion) = actualizar { it.copy(tipo = valor) }
    fun onDestinoChange(valor: String) = actualizar { it.copy(destino = valor) }
    fun onDesdeChange(valor: LocalDate) = actualizar { it.copy(desde = valor) }
    fun onHastaChange(valor: LocalDate) = actualizar { it.copy(hasta = valor) }
    fun onCentroCostoChange(valor: String) = actualizar { it.copy(centroCosto = valor) }
    fun onObservacionChange(valor: String) = actualizar { it.copy(observacion = valor) }

    fun onGuardar() {
        intentoGuardar = true
        val actual = _estado.value
        val errores = validar(actual)
        _estado.update { it.copy(errores = errores) }
        if (errores.hayErrores()) return                 // hay errores: no se guarda

        viewModelScope.launch {
            val usuarioId = session.usuarioId.first() ?: return@launch
            _estado.update { it.copy(guardando = true) }
            val id = rendicionRepo.crear(
                Rendicion(
                    numero = "",                          // el repositorio asigna RND-2026-0000NN
                    usuarioId = usuarioId,
                    titulo = actual.titulo.trim(),
                    tipo = actual.tipo,
                    destino = actual.destino.trim().takeIf { actual.tipo == TipoRendicion.COMISION_SERVICIO },
                    desde = actual.desde!!,               // !! es seguro: el validador ya revisó que no sean null
                    hasta = actual.hasta!!,
                    centroCosto = actual.centroCosto!!,
                    observacion = actual.observacion.trim().ifBlank { null }
                )
            )
            _estado.update { it.copy(guardando = false, guardadaId = id) }
        }
    }

    // Aplica un cambio al estado y, si ya se intentó guardar, revalida todo
    private fun actualizar(cambio: (NuevaRendicionUiState) -> NuevaRendicionUiState) {
        _estado.update { actual ->
            val nuevo = cambio(actual)
            if (intentoGuardar) nuevo.copy(errores = validar(nuevo)) else nuevo
        }
    }

    private fun validar(e: NuevaRendicionUiState) = RendicionErrores(
        titulo = RendicionValidator.validarTitulo(e.titulo),
        destino = RendicionValidator.validarDestino(e.tipo, e.destino),
        periodo = RendicionValidator.validarPeriodo(e.desde, e.hasta),
        centroCosto = RendicionValidator.validarCentroCosto(e.centroCosto),
        observacion = RendicionValidator.validarObservacion(e.observacion)
    )

    private fun RendicionErrores.hayErrores() =
        listOfNotNull(titulo, destino, periodo, centroCosto, observacion).isNotEmpty()
}
