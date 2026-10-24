package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Los 3 estados posibles de la sesión
sealed interface EstadoSesion {
    data object Cargando : EstadoSesion                     // leyendo DataStore al abrir la app
    data object SinSesion : EstadoSesion                    // mostrar Login
    data class Activa(val usuario: Usuario) : EstadoSesion  // mostrar la app de su rol
}

class SesionViewModel(
    private val session: SessionManager,
    private val usuarioRepo: UsuarioRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val estado: StateFlow<EstadoSesion> = session.usuarioId
        .flatMapLatest { id ->
            if (id == null) flowOf(EstadoSesion.SinSesion)
            else usuarioRepo.obtener(id).map { usuario ->
                if (usuario == null) EstadoSesion.SinSesion else EstadoSesion.Activa(usuario)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoSesion.Cargando)

    // El tema lo usará tu compañera en Groupe7AppTheme (paso 6)
    val temaOscuro: StateFlow<Boolean?> = session.temaOscuro
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun cerrarSesion() {
        viewModelScope.launch { session.cerrarSesion() }
    }
}