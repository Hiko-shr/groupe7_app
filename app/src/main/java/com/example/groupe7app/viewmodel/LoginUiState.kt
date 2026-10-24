package com.example.groupe7app.viewmodel

import com.example.groupe7app.model.enums.Rol

// Un mensaje por campo; null = sin error (igual que UsuarioErrores del profesor)
data class LoginErrores(
    val correo: String? = null,
    val clave: String? = null
)

// TODO lo que la pantalla muestra
data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val errores: LoginErrores = LoginErrores(),
    val errorGeneral: String? = null,      // "Correo o contraseña incorrectos"
    val intentoEnviar: Boolean = false,    // después del primer intento, se valida mientras escribe
    val cargando: Boolean = false,
    val rolIngresado: Rol? = null          // al ingresar bien, la pantalla navega según el rol
)