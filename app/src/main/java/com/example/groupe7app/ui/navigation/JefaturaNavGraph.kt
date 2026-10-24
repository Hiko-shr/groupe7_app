package com.example.groupe7app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
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
            composable(Screen.JefBandeja.route) { EnConstruccion("Bandeja de aprobación") }
            composable(Screen.Perfil.route) { PerfilScreen(usuario, onCerrarSesion) }
        }
    }
}