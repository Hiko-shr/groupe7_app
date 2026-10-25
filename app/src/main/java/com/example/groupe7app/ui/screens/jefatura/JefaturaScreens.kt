package com.example.groupe7app.ui.screens.jefatura

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groupe7app.viewmodel.BandejaJefaturaViewModel
import com.example.groupe7app.viewmodel.RevisionGastoViewModel
import com.example.groupe7app.viewmodel.RevisionRendicionViewModel

// Conexiones ViewModel ↔ diseño de las 3 pantallas de jefatura

@Composable
fun BandejaJefaturaScreen(
    viewModel: BandejaJefaturaViewModel,
    onAbrirRendicion: (Long) -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    BandejaJefaturaContent(estado = estado, onAbrirRendicion = onAbrirRendicion)
}

@Composable
fun RevisionRendicionScreen(
    viewModel: RevisionRendicionViewModel,
    rendicionId: Long,
    onVolver: () -> Unit,
    onAbrirGasto: (Long) -> Unit
) {
    LaunchedEffect(rendicionId) { viewModel.cargar(rendicionId) }
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Al finalizar, vuelve a la bandeja (donde ya no aparece)
    LaunchedEffect(estado.finalizada) { if (estado.finalizada) onVolver() }

    RevisionRendicionContent(
        estado = estado,
        onVolver = onVolver,
        onAbrirGasto = onAbrirGasto,
        onFinalizarClick = viewModel::onFinalizarClick,
        onConfirmar = viewModel::onConfirmar,
        onCancelar = viewModel::onCancelar
    )
}

@Composable
fun RevisionGastoScreen(
    viewModel: RevisionGastoViewModel,
    gastoId: Long,
    onVolver: () -> Unit
) {
    LaunchedEffect(gastoId) { viewModel.cargar(gastoId) }
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Al guardar la decisión, vuelve a la rendición
    LaunchedEffect(estado.guardado) { if (estado.guardado) onVolver() }

    RevisionGastoContent(
        estado = estado,
        onVolver = onVolver,
        onDecisionChange = viewModel::onDecisionChange,
        onMontoAprobadoChange = viewModel::onMontoAprobadoChange,
        onComentarioChange = viewModel::onComentarioChange,
        onMotivoChange = viewModel::onMotivoChange,
        onGuardar = viewModel::onGuardar
    )
}
