package com.example.groupe7app.ui.screens.tecnico

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groupe7app.viewmodel.NuevaRendicionViewModel

// Conecta el ViewModel con el diseño (NuevaRendicionContent)
@Composable
fun NuevaRendicionScreen(
    viewModel: NuevaRendicionViewModel,
    onVolver: () -> Unit,
    onCreada: (Long) -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Cuando el ViewModel avisa que se guardó, la navegación abre el detalle
    LaunchedEffect(estado.guardadaId) {
        estado.guardadaId?.let(onCreada)
    }

    NuevaRendicionContent(
        estado = estado,
        onTituloChange = viewModel::onTituloChange,
        onTipoChange = viewModel::onTipoChange,
        onDestinoChange = viewModel::onDestinoChange,
        onDesdeChange = viewModel::onDesdeChange,
        onHastaChange = viewModel::onHastaChange,
        onCentroCostoChange = viewModel::onCentroCostoChange,
        onObservacionChange = viewModel::onObservacionChange,
        onGuardar = viewModel::onGuardar,
        onVolver = onVolver
    )
}
