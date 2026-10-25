package com.example.groupe7app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.ui.screens.comun.EnConstruccion
import com.example.groupe7app.ui.screens.perfil.PerfilScreen
import com.example.groupe7app.ui.screens.tecnico.TecInicioScreen
import com.example.groupe7app.viewmodel.ViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groupe7app.ui.screens.tecnico.MisRendicionesScreen
import com.example.groupe7app.viewmodel.MisRendicionesViewModel

@Composable
fun TecnicoNavGraph(factory: ViewModelFactory, usuario: Usuario, onCerrarSesion: () -> Unit) {
    val navController = rememberNavController()
    val items = listOf(
        ItemBarra(Screen.TecInicio, "Inicio", Icons.Filled.Home),
        ItemBarra(Screen.TecRendiciones, "Rendiciones", Icons.AutoMirrored.Filled.List),
        ItemBarra(Screen.Perfil, "Perfil", Icons.Filled.Person)
    )

    Scaffold(bottomBar = { BarraInferior(navController, items) }) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.TecInicio.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.TecInicio.route) {
                TecInicioScreen(
                    onNavegarRendiciones = { navController.navigate(Screen.TecRendiciones.route) },
                    onNavegarNuevoGasto = { navController.navigate(Screen.TecNuevaRendicion.route) }
                )
            }
                composable(Screen.TecRendiciones.route) {
                val vm: MisRendicionesViewModel = viewModel(factory = factory)
                MisRendicionesScreen(
                    viewModel = vm,
                    onAbrirRendicion = { id -> navController.navigate(Screen.TecDetalle.crear(id)) },
                    onNuevaRendicion = { navController.navigate(Screen.TecNuevaRendicion.route) }
                )
            }
            composable(Screen.TecDetalle.route) { EnConstruccion("Detalle de rendición") }
            composable(Screen.TecNuevaRendicion.route) { EnConstruccion("Nueva rendición") }
            composable(Screen.Perfil.route) { PerfilScreen(usuario, onCerrarSesion) }
        }
    }
}

