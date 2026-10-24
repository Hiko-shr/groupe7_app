package com.example.groupe7app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groupe7app.model.enums.Rol
import com.example.groupe7app.ui.screens.comun.PantallaPendiente
import com.example.groupe7app.ui.screens.login.LoginScreen
import com.example.groupe7app.viewmodel.EstadoSesion
import com.example.groupe7app.viewmodel.LoginViewModel
import com.example.groupe7app.viewmodel.SesionViewModel
import com.example.groupe7app.viewmodel.ViewModelFactory

@Composable
fun AppNavigation(factory: ViewModelFactory) {
    val sesionVm: SesionViewModel = viewModel(factory = factory)
    val estado by sesionVm.estado.collectAsStateWithLifecycle()

    when (val sesion = estado) {
        EstadoSesion.Cargando -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        EstadoSesion.SinSesion -> {
            val loginVm: LoginViewModel = viewModel(factory = factory)
            LaunchedEffect(Unit) { loginVm.limpiar() }       // formulario vacío cada vez que aparece
            LoginScreen(loginVm, onIngresoExitoso = { })     // la sesión se encarga de cambiar de pantalla
        }

        is EstadoSesion.Activa -> {
            val usuario = sesion.usuario
            when (usuario.rol) {
                // En el paso 8c cada uno se reemplaza por su grafo: TecnicoNavGraph, JefaturaNavGraph...
                Rol.TECNICO -> PantallaPendiente("Técnico", usuario.nombre, sesionVm::cerrarSesion)
                Rol.JEFATURA -> PantallaPendiente("Jefatura", usuario.nombre, sesionVm::cerrarSesion)
                Rol.FINANZAS -> PantallaPendiente("Finanzas", usuario.nombre, sesionVm::cerrarSesion)
            }
        }
    }
}