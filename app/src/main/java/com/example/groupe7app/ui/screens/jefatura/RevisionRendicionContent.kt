package com.example.groupe7app.ui.screens.jefatura

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groupe7app.model.enums.EstadoRendicion
import com.example.groupe7app.ui.components.ChipEstado
import com.example.groupe7app.ui.components.TarjetaGasto
import com.example.groupe7app.ui.components.formatoClp
import com.example.groupe7app.ui.components.formatoFecha
import com.example.groupe7app.viewmodel.RevisionRendicionUiState

// Revisión de una rendición: la jefatura abre cada gasto, decide, y al final cierra la revisión
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionRendicionContent(
    estado: RevisionRendicionUiState,
    onVolver: () -> Unit,
    onAbrirGasto: (Long) -> Unit,
    onFinalizarClick: () -> Unit,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    val rendicion = estado.rendicion

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(rendicion?.numero ?: "Revisión") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            if (rendicion?.estado == EstadoRendicion.ENVIADA) {
                Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                    if (!estado.puedeFinalizar) {
                        Text("Resuelve todos los gastos para finalizar la revisión",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                    }
                    Button(
                        onClick = onFinalizarClick,
                        enabled = estado.puedeFinalizar,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) { Text("Finalizar revisión") }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (rendicion == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---------- Resumen ----------
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(rendicion.titulo, style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            ChipEstado(estado = rendicion.estado)
                        }
                        Text("Técnico: ${estado.tecnico}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            rendicion.tipo.etiqueta + (rendicion.destino?.let { " · $it" } ?: ""),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${formatoFecha(rendicion.desde)} al ${formatoFecha(rendicion.hasta)} · ${rendicion.centroCosto}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text("Total: ${formatoClp(estado.total)}", style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ---------- Avance de la revisión ----------
            if (estado.gastos.isNotEmpty()) {
                item {
                    Column {
                        Text("Revisados ${estado.resueltos} de ${estado.gastos.size}",
                            style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { estado.resueltos.toFloat() / estado.gastos.size },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ---------- Gastos ----------
            items(estado.gastos, key = { it.gasto.id }) { item ->
                TarjetaGasto(item = item, onClick = { onAbrirGasto(item.gasto.id) })
            }
        }
    }

    // ---------- Confirmación ----------
    if (estado.mostrarConfirmacion) {
        val mensaje = when (estado.resultado) {
            EstadoRendicion.OBSERVADA -> "Hay gastos observados: la rendición vuelve al técnico para que los corrija."
            EstadoRendicion.RECHAZADA -> "Todos los gastos fueron rechazados: la rendición quedará rechazada."
            else -> "La rendición quedará aprobada y pasará a Finanzas."
        }
        AlertDialog(
            onDismissRequest = onCancelar,
            title = { Text("¿Finalizar revisión?") },
            text = { Text(mensaje) },
            confirmButton = { TextButton(onClick = onConfirmar) { Text("Finalizar") } },
            dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
        )
    }
}
