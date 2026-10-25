package com.example.groupe7app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Payments
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
import com.example.groupe7app.ui.screens.finanzas.BandejaFinanzasScreen
import com.example.groupe7app.ui.screens.finanzas.PagoScreen
import com.example.groupe7app.ui.screens.finanzas.RevisionFinanzasScreen
import com.example.groupe7app.ui.screens.perfil.PerfilScreen
import com.example.groupe7app.viewmodel.BandejaFinanzasViewModel
import com.example.groupe7app.viewmodel.PagoViewModel
import com.example.groupe7app.viewmodel.RevisionFinanzasViewModel
import com.example.groupe7app.viewmodel.SesionViewModel
import com.example.groupe7app.viewmodel.ViewModelFactory

@Composable
fun FinanzasNavGraph(factory: ViewModelFactory, usuario: Usuario, onCerrarSesion: () -> Unit) {
    val navController = rememberNavController()
    val items = listOf(
        ItemBarra(Screen.FinBandeja, "Revisión", Icons.Filled.FactCheck),
        ItemBarra(Screen.FinPago, "Pagos", Icons.Filled.Payments),
        ItemBarra(Screen.Perfil, "Perfil", Icons.Filled.Person)
    )

    Scaffold(bottomBar = { BarraInferior(navController, items) }) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.FinBandeja.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.FinBandeja.route) {
                val vm: BandejaFinanzasViewModel = viewModel(factory = factory)
                BandejaFinanzasScreen(
                    viewModel = vm,
                    onAbrirRendicion = { id -> navController.navigate(Screen.FinRevision.crear(id)) }
                )
            }

            composable(
                Screen.FinRevision.route,
                arguments = listOf(navArgument("rendicionId") { type = NavType.LongType })
            ) { entrada ->
                val rendicionId = entrada.arguments?.getLong("rendicionId") ?: 0L
                val vm: RevisionFinanzasViewModel = viewModel(factory = factory)
                RevisionFinanzasScreen(viewModel = vm, rendicionId = rendicionId,
                    onVolver = { navController.popBackStack() })
            }

            composable(Screen.FinPago.route) {
                val vm: PagoViewModel = viewModel(factory = factory)
                PagoScreen(viewModel = vm)
            }

            composable(Screen.Perfil.route) {
                val sesionVm: SesionViewModel = viewModel(factory = factory)
                PerfilScreen(usuario, sesionVm, onCerrarSesion)
            }
        }
    }
}
