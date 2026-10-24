package com.example.groupe7app.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// Un botón de la barra: a qué pantalla lleva, su texto y su ícono
data class ItemBarra(val screen: Screen, val etiqueta: String, val icono: ImageVector)

@Composable
fun BarraInferior(navController: NavHostController, items: List<ItemBarra>) {
    // Ruta de la pantalla que se está mostrando (para marcar el botón activo)
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = rutaActual == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        // Evita apilar pantallas al tocar varias veces la barra
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icono, contentDescription = null) },
                label = { Text(item.etiqueta) }
            )
        }
    }
}