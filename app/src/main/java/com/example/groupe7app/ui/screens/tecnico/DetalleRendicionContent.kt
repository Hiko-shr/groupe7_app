package com.example.groupe7app.ui.screens.tecnico

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groupe7app.ui.components.ChipEstado
import com.example.groupe7app.ui.components.TarjetaGasto
import com.example.groupe7app.ui.components.formatoClp
import com.example.groupe7app.ui.components.formatoFecha
import com.example.groupe7app.viewmodel.DetalleRendicionUiState
import java.time.format.DateTimeFormatter

// Diseño de la compañera, conectado al DetalleRendicionUiState.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRendicionContent(
    estado: DetalleRendicionUiState,
    onVolver: () -> Unit,
    onAgregarGasto: () -> Unit,
    onGastoClick: (Long) -> Unit,
    onEnviarClick: () -> Unit,
    onConfirmarEnvio: () -> Unit,
    onCancelarEnvio: () -> Unit
) {
    val rendicion = estado.rendicion

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(rendicion?.numero ?: "Detalle de rendición") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            // Solo se agregan gastos si la rendición está en borrador u observada
            if (estado.editable) {
                FloatingActionButton(
                    onClick = onAgregarGasto,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar gasto")
                }
            }
        },
        bottomBar = {
            if (estado.editable) {
                Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                    estado.errorEnvio?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Error, contentDescription = null,
                                tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Button(onClick = onEnviarClick, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                        Text("Enviar a jefatura")
                    }
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
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---------- Tarjeta de resumen ----------
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(rendicion.titulo, style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            ChipEstado(estado = rendicion.estado)
                        }
                        Text(
                            "${rendicion.tipo.etiqueta}" + (rendicion.destino?.let { " · $it" } ?: ""),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${formatoFecha(rendicion.desde)} al ${formatoFecha(rendicion.hasta)} · ${rendicion.centroCosto}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        HorizontalDivider()
                        estado.totalesPorMedio.forEach { (medio, monto) ->
                            FilaMonto(medio.etiqueta, monto)
                        }
                        if (estado.exceso > 0) FilaMonto("Exceso sobre topes", -estado.exceso)
                        FilaMonto("Reembolso estimado", estado.reembolsoEstimado)
                        Text(
                            text = "Total: ${formatoClp(estado.total)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ---------- Lista de gastos ----------
            item {
                Text("Gastos ingresados (${estado.gastos.size})",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            if (estado.gastos.isEmpty()) {
                item {
                    Text("Aún no hay gastos en esta rendición.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(estado.gastos, key = { "gasto-${it.gasto.id}" }) { item ->
                    TarjetaGasto(item = item, onClick = { onGastoClick(item.gasto.id) })
                }
            }

            // ---------- Historial (sale de la tabla historial_estados) ----------
            if (estado.historial.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text("Historial", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(estado.historial, key = { "historial-${it.id}" }) { h ->
                    Column {
                        Text(
                            "${h.estadoNuevo.lowercase().replaceFirstChar { it.uppercase() }.replace('_', ' ')}" +
                                    (h.comentario?.let { " · $it" } ?: ""),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(h.fecha.format(FORMATO_FECHA_HORA), style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    // ---------- Confirmación antes de enviar ----------
    if (estado.mostrarConfirmacion && rendicion != null) {
        AlertDialog(
            onDismissRequest = onCancelarEnvio,
            title = { Text("¿Enviar a jefatura?") },
            text = {
                Text("Se enviarán ${estado.gastos.size} gasto(s) por ${formatoClp(estado.total)}. " +
                        "Después no podrás modificarlos mientras estén en revisión.")
            },
            confirmButton = { TextButton(onClick = onConfirmarEnvio) { Text("Enviar") } },
            dismissButton = { TextButton(onClick = onCancelarEnvio) { Text("Cancelar") } }
        )
    }
}

private val FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

@Composable
private fun FilaMonto(etiqueta: String, monto: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium)
        Text(formatoClp(monto), style = MaterialTheme.typography.bodyMedium)
    }
}
