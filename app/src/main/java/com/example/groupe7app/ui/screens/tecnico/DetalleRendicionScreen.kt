package com.example.groupe7app.ui.screens.tecnico

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groupe7app.viewmodel.DetalleRendicionViewModel

// Conecta el ViewModel con el diseño (DetalleRendicionContent)
@Composable
fun DetalleRendicionScreen(
    viewModel: DetalleRendicionViewModel,
    rendicionId: Long,
    onVolver: () -> Unit,
    onAgregarGasto: () -> Unit,
    onAbrirGasto: (Long) -> Unit
) {
    // Le dice al ViewModel qué rendición cargar (el id viene en la ruta)
    LaunchedEffect(rendicionId) { viewModel.cargar(rendicionId) }

    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Después de enviarla volvemos a la lista, donde ya aparece "En aprobación"
    LaunchedEffect(estado.enviada) {
        if (estado.enviada) onVolver()
    }

    DetalleRendicionContent(
        estado = estado,
        onVolver = onVolver,
        onAgregarGasto = onAgregarGasto,
        onGastoClick = onAbrirGasto,
        onEnviarClick = viewModel::onEnviarClick,
        onConfirmarEnvio = viewModel::onConfirmarEnvio,
        onCancelarEnvio = viewModel::onCancelarEnvio
    )
}
