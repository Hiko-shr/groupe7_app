package com.example.groupe7app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.groupe7app.ui.theme.TeqDarkBlue

sealed class DestinoNavegacion(val ruta: String, val titulo: String, val icono: String) {
    object Inicio : DestinoNavegacion("inicio", "Inicio", "🏠")
    object Rendiciones : DestinoNavegacion("rendiciones", "Rendiciones", "📄")
    object Perfil : DestinoNavegacion("perfil", "Perfil", "👤")
}

@Composable
fun BottomNavigationBar(
    rutaActual: String?,
    onNavegar: (String) -> Unit
) {
    val items = listOf(
        DestinoNavegacion.Inicio,
        DestinoNavegacion.Rendiciones,
        DestinoNavegacion.Perfil
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = TeqDarkBlue
    ) {
        items.forEach { destino ->
            val seleccionado = rutaActual == destino.ruta
            NavigationBarItem(
                selected = seleccionado,
                onClick = { onNavegar(destino.ruta) },
                icon = { Text(destino.icono, fontSize = 20.sp) },
                label = { Text(destino.titulo) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}