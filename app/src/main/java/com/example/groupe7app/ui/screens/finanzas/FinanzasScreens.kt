package com.example.groupe7app.ui.screens.finanzas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groupe7app.viewmodel.BandejaFinanzasViewModel
import com.example.groupe7app.viewmodel.PagoViewModel
import com.example.groupe7app.viewmodel.RevisionFinanzasViewModel

// Conexiones ViewModel ↔ diseño de las 3 pantallas de Finanzas

@Composable
fun BandejaFinanzasScreen(viewModel: BandejaFinanzasViewModel, onAbrirRendicion: (Long) -> Unit) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    BandejaFinanzasContent(estado = estado, onAbrirRendicion = onAbrirRendicion)
}

@Composable
fun RevisionFinanzasScreen(viewModel: RevisionFinanzasViewModel, rendicionId: Long, onVolver: () -> Unit) {
    LaunchedEffect(rendicionId) { viewModel.cargar(rendicionId) }
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Al aprobar o rechazar, vuelve a la bandeja (donde ya no aparece)
    LaunchedEffect(estado.finalizada) { if (estado.finalizada) onVolver() }

    RevisionFinanzasContent(
        estado = estado,
        onVolver = onVolver,
        onAbrirDialogo = viewModel::onAbrirDialogo,
        onCerrarDialogo = viewModel::onCerrarDialogo,
        onComentarioChange = viewModel::onComentarioChange,
        onConfirmar = viewModel::onConfirmar
    )
}

@Composable
fun PagoScreen(viewModel: PagoViewModel) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    PagoContent(
        estado = estado,
        onSeleccionar = viewModel::onSeleccionar,
        onNumeroOperacionChange = viewModel::onNumeroOperacionChange,
        onObservacionChange = viewModel::onObservacionChange,
        onRegistrar = viewModel::onRegistrar
    )
}
