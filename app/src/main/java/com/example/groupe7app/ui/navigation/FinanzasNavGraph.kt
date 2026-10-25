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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.groupe7app.model.entity.Usuario
import com.example.groupe7app.ui.screens.comun.EnConstruccion
import com.example.groupe7app.ui.screens.perfil.PerfilScreen
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
            composable(Screen.FinBandeja.route) { EnConstruccion("Revisión final") }
            composable(Screen.FinPago.route) { EnConstruccion("Registrar pago") }
            composable(Screen.Perfil.route) {
                val sesionVm: SesionViewModel = viewModel(factory = factory)
                PerfilScreen(usuario, sesionVm, onCerrarSesion)
            }
        }
    }
}

