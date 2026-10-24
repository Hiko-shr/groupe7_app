package com.example.groupe7app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.UsuarioRepository
import com.example.groupe7app.model.validation.LoginValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val usuarioRepo: UsuarioRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                correo = valor,
                errorGeneral = null,
                errores = it.errores.copy(
                    correo = if (it.intentoEnviar) LoginValidator.validarCorreo(valor) else null
                )
            )
        }
    }

    fun onClaveChange(valor: String) {
        _estado.update {
            it.copy(
                clave = valor,
                errorGeneral = null,
                errores = it.errores.copy(
                    clave = if (it.intentoEnviar) LoginValidator.validarClave(valor) else null
                )
            )
        }
    }

    fun onIngresar() {
        val actual = _estado.value
        val errores = LoginErrores(
            correo = LoginValidator.validarCorreo(actual.correo),
            clave = LoginValidator.validarClave(actual.clave)
        )
        _estado.update { it.copy(errores = errores, intentoEnviar = true, errorGeneral = null) }
        if (errores.correo != null || errores.clave != null) return   // hay errores: no seguir

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val usuario = usuarioRepo.autenticar(actual.correo, actual.clave)
            if (usuario == null) {
                _estado.update { it.copy(cargando = false, errorGeneral = "Correo o contraseña incorrectos") }
            } else {
                session.iniciarSesion(usuario.id)                    // queda guardado en DataStore
                _estado.update { it.copy(cargando = false, rolIngresado = usuario.rol) }
            }
        }
    }
}