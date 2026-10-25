package com.example.groupe7app.ui.screens.tecnico

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groupe7app.viewmodel.MisRendicionesViewModel

// Conecta el ViewModel con el diseño de tu compañera
@Composable
fun MisRendicionesScreen(
    viewModel: MisRendicionesViewModel,
    onAbrirRendicion: (Long) -> Unit,
    onNuevaRendicion: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    MisRendicionesContent(
        estado = estado,
        onFiltro = viewModel::onFiltro,
        onAbrirRendicion = onAbrirRendicion,
        onNuevaRendicion = onNuevaRendicion
    )
}