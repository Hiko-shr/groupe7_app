package com.example.groupe7app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
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
import com.example.groupe7app.ui.screens.jefatura.BandejaJefaturaScreen
import com.example.groupe7app.ui.screens.jefatura.RevisionGastoScreen
import com.example.groupe7app.ui.screens.jefatura.RevisionRendicionScreen
import com.example.groupe7app.ui.screens.perfil.PerfilScreen
import com.example.groupe7app.viewmodel.BandejaJefaturaViewModel
import com.example.groupe7app.viewmodel.RevisionGastoViewModel
import com.example.groupe7app.viewmodel.RevisionRendicionViewModel
import com.example.groupe7app.viewmodel.SesionViewModel
import com.example.groupe7app.viewmodel.ViewModelFactory

@Composable
fun JefaturaNavGraph(factory: ViewModelFactory, usuario: Usuario, onCerrarSesion: () -> Unit) {
    val navController = rememberNavController()
    val items = listOf(
        ItemBarra(Screen.JefBandeja, "Bandeja", Icons.Filled.Inbox),
        ItemBarra(Screen.Perfil, "Perfil", Icons.Filled.Person)
    )

    Scaffold(bottomBar = { BarraInferior(navController, items) }) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.JefBandeja.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.JefBandeja.route) {
                val vm: BandejaJefaturaViewModel = viewModel(factory = factory)
                BandejaJefaturaScreen(
                    viewModel = vm,
                    onAbrirRendicion = { id -> navController.navigate(Screen.JefRevision.crear(id)) }
                )
            }

            composable(
                Screen.JefRevision.route,
                arguments = listOf(navArgument("rendicionId") { type = NavType.LongType })
            ) { entrada ->
                val rendicionId = entrada.arguments?.getLong("rendicionId") ?: 0L
                val vm: RevisionRendicionViewModel = viewModel(factory = factory)
                RevisionRendicionScreen(
                    viewModel = vm,
                    rendicionId = rendicionId,
                    onVolver = { navController.popBackStack() },
                    onAbrirGasto = { gastoId -> navController.navigate(Screen.JefRevisionGasto.crear(rendicionId, gastoId)) }
                )
            }

            composable(
                Screen.JefRevisionGasto.route,
                arguments = listOf(
                    navArgument("rendicionId") { type = NavType.LongType },
                    navArgument("gastoId") { type = NavType.LongType }
                )
            ) { entrada ->
                val gastoId = entrada.arguments?.getLong("gastoId") ?: 0L
                val vm: RevisionGastoViewModel = viewModel(factory = factory)
                RevisionGastoScreen(viewModel = vm, gastoId = gastoId, onVolver = { navController.popBackStack() })
            }

            composable(Screen.Perfil.route) {
                val sesionVm: SesionViewModel = viewModel(factory = factory)
                PerfilScreen(usuario, sesionVm, onCerrarSesion)
            }
        }
    }
}
