package com.example.groupe7app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.ui.screens.comun.EnConstruccion
import com.example.groupe7app.ui.screens.perfil.PerfilScreen
import com.example.groupe7app.ui.screens.tecnico.DetalleRendicionScreen
import com.example.groupe7app.ui.screens.tecnico.InicioTecnicoScreen
import com.example.groupe7app.ui.screens.tecnico.MisRendicionesScreen
import com.example.groupe7app.ui.screens.tecnico.NuevaRendicionScreen
import com.example.groupe7app.viewmodel.DetalleRendicionViewModel
import com.example.groupe7app.viewmodel.InicioTecnicoViewModel
import com.example.groupe7app.viewmodel.MisRendicionesViewModel
import com.example.groupe7app.viewmodel.NuevaRendicionViewModel
import com.example.groupe7app.viewmodel.ViewModelFactory

@Composable
fun TecnicoNavGraph(factory: ViewModelFactory, usuario: Usuario, onCerrarSesion: () -> Unit) {
    val navController = rememberNavController()
    val items = listOf(
        ItemBarra(Screen.TecInicio, "Inicio", Icons.Filled.Home),
        ItemBarra(Screen.TecRendiciones, "Rendiciones", Icons.AutoMirrored.Filled.List),
        ItemBarra(Screen.Perfil, "Perfil", Icons.Filled.Person)
    )

    // El id de la rendición viaja en la ruta como número (Long)
    val argumentoRendicion = listOf(navArgument("rendicionId") { type = NavType.LongType })

    Scaffold(bottomBar = { BarraInferior(navController, items) }) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.TecInicio.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.TecInicio.route) {
                val vm: InicioTecnicoViewModel = viewModel(factory = factory)
                InicioTecnicoScreen(
                    viewModel = vm,
                    onVerRendiciones = { navController.navigate(Screen.TecRendiciones.route) },
                    onAbrirRendicion = { id -> navController.navigate(Screen.TecDetalle.crear(id)) },
                    onNuevaRendicion = { navController.navigate(Screen.TecNuevaRendicion.route) },
                    onNuevoGasto = { id -> navController.navigate(Screen.TecGasto.crear(id)) }
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

            composable(Screen.TecNuevaRendicion.route) {
                val vm: NuevaRendicionViewModel = viewModel(factory = factory)
                NuevaRendicionScreen(
                    viewModel = vm,
                    onVolver = { navController.popBackStack() },
                    onCreada = { id ->
                        navController.navigate(Screen.TecDetalle.crear(id)) {
                            // al volver desde el detalle, no reaparece el formulario
                            popUpTo(Screen.TecNuevaRendicion.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.TecDetalle.route, arguments = argumentoRendicion) { entrada ->
                val id = entrada.arguments?.getLong("rendicionId") ?: 0L
                val vm: DetalleRendicionViewModel = viewModel(factory = factory)
                DetalleRendicionScreen(
                    viewModel = vm,
                    rendicionId = id,
                    onVolver = { navController.popBackStack() },
                    onAgregarGasto = { navController.navigate(Screen.TecGasto.crear(id)) }
                )
            }

            composable(Screen.TecGasto.route, arguments = argumentoRendicion) {
                EnConstruccion("Formulario de gasto")
            }

            composable(Screen.Perfil.route) { PerfilScreen(usuario, onCerrarSesion) }
        }
    }
}
