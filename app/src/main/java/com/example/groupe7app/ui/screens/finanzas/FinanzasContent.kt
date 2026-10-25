package com.example.groupe7app.ui.screens.finanzas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.groupe7app.ui.components.CampoValidado
import com.example.groupe7app.ui.components.ChipEstado
import com.example.groupe7app.ui.components.EstadoVacio
import com.example.groupe7app.ui.components.TarjetaGasto
import com.example.groupe7app.ui.components.formatoClp
import com.example.groupe7app.ui.components.formatoFecha
import com.example.groupe7app.viewmodel.BandejaFinanzasUiState
import com.example.groupe7app.viewmodel.DialogoFinanzas
import com.example.groupe7app.viewmodel.PagoUiState
import com.example.groupe7app.viewmodel.RendicionFinanzas
import com.example.groupe7app.viewmodel.RevisionFinanzasUiState
import java.time.LocalDate

// =====================================================================
// 1) Bandeja de Finanzas: rendiciones aprobadas por la jefatura
// =====================================================================
@Composable
fun BandejaFinanzasContent(
    estado: BandejaFinanzasUiState,
    onAbrirRendicion: (Long) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            Encabezado(
                titulo = "Revisión final",
                subtitulo = when (estado.rendiciones.size) {
                    0 -> "No hay rendiciones por revisar"
                    1 -> "1 rendición aprobada por jefatura"
                    else -> "${estado.rendiciones.size} rendiciones aprobadas por jefatura"
                }
            )
            when {
                estado.cargando -> Cargando()
                estado.rendiciones.isEmpty() -> EstadoVacio(mensaje = "No hay rendiciones esperando la revisión de Finanzas.")
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(estado.rendiciones, key = { it.rendicion.id }) { r ->
                        TarjetaFinanzas(r, onClick = { onAbrirRendicion(r.rendicion.id) })
                    }
                }
            }
        }
    }
}

// =====================================================================
// 2) Revisión final de una rendición
// =====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionFinanzasContent(
    estado: RevisionFinanzasUiState,
    onVolver: () -> Unit,
    onAbrirDialogo: (DialogoFinanzas) -> Unit,
    onCerrarDialogo: () -> Unit,
    onComentarioChange: (String) -> Unit,
    onConfirmar: () -> Unit
) {
    val rendicion = estado.rendicion

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(rendicion?.numero ?: "Revisión final") },
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
            if (estado.editable) {
                Row(
                    Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onAbrirDialogo(DialogoFinanzas.RECHAZAR) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) { Text("Rechazar") }
                    Button(
                        onClick = { onAbrirDialogo(DialogoFinanzas.APROBAR) },
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) { Text("Aprobar") }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (rendicion == null) {
            Box(Modifier.fillMaxSize().padding(padding)) { Cargando() }
        } else LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                            "${formatoFecha(rendicion.desde)} al ${formatoFecha(rendicion.hasta)} · ${rendicion.centroCosto}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        HorizontalDivider()
                        FilaMonto("Aprobado por jefatura", estado.aprobado)
                        FilaMonto("A reembolsar al técnico", estado.reembolso, destacado = true)
                    }
                }
            }
            item {
                Text("Gastos (${estado.gastos.size})", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            }
            items(estado.gastos, key = { it.gasto.id }) { item ->
                TarjetaGasto(item = item, onClick = { })
            }
        }
    }

    // ---------- Diálogos de confirmación ----------
    when (estado.dialogo) {
        DialogoFinanzas.APROBAR -> AlertDialog(
            onDismissRequest = onCerrarDialogo,
            title = { Text("¿Aprobar rendición?") },
            text = { Text("Quedará lista para pagar. Reembolso al técnico: ${formatoClp(estado.reembolso)}.") },
            confirmButton = { TextButton(onClick = onConfirmar) { Text("Aprobar") } },
            dismissButton = { TextButton(onClick = onCerrarDialogo) { Text("Cancelar") } }
        )
        DialogoFinanzas.RECHAZAR -> AlertDialog(
            onDismissRequest = onCerrarDialogo,
            title = { Text("Rechazar rendición") },
            text = {
                CampoValidado(
                    valor = estado.comentario,
                    onValorChange = onComentarioChange,
                    etiqueta = "Motivo del rechazo",
                    error = estado.errorComentario,
                    unaLinea = false,
                    contadorMax = 200
                )
            },
            confirmButton = { TextButton(onClick = onConfirmar) { Text("Rechazar") } },
            dismissButton = { TextButton(onClick = onCerrarDialogo) { Text("Cancelar") } }
        )
        null -> {}
    }
}

// =====================================================================
// 3) Registrar pago
// =====================================================================
@Composable
fun PagoContent(
    estado: PagoUiState,
    onSeleccionar: (Long) -> Unit,
    onNumeroOperacionChange: (String) -> Unit,
    onObservacionChange: (String) -> Unit,
    onRegistrar: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Encabezado(
                    titulo = "Registrar pago",
                    subtitulo = "Elige las rendiciones de un técnico que pagaste en una transferencia"
                )
            }

            // Confirmación del último pago
            estado.mensaje?.let { mensaje ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(Modifier.width(12.dp))
                            Text(mensaje, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }

            when {
                estado.cargando -> item { Cargando() }
                estado.pendientes.isEmpty() -> item {
                    Text("No hay rendiciones aprobadas pendientes de pago.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> {
                    items(estado.pendientes, key = { it.rendicion.id }) { r ->
                        TarjetaFinanzas(
                            r,
                            onClick = { onSeleccionar(r.rendicion.id) },
                            seleccionada = r.rendicion.id in estado.seleccionadas
                        )
                    }
                    estado.errores.seleccion?.let { item { MensajeErrorFinanzas(it) } }

                    // ---------- Datos de la transferencia ----------
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Spacer(Modifier.height(4.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    FilaMonto("Total a transferir", estado.total, destacado = true)
                                    Text("Fecha: ${formatoFecha(LocalDate.now())}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            CampoValidado(
                                valor = estado.numeroOperacion,
                                onValorChange = onNumeroOperacionChange,
                                etiqueta = "N.º de operación de la transferencia",
                                error = estado.errores.numeroOperacion
                            )
                            CampoValidado(
                                valor = estado.observacion,
                                onValorChange = onObservacionChange,
                                etiqueta = "Observación (opcional)",
                                error = estado.errores.observacion,
                                unaLinea = false,
                                contadorMax = 200
                            )
                            Button(
                                onClick = onRegistrar,
                                enabled = !estado.guardando,
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                if (estado.guardando) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                                else Text("Registrar pago")
                            }
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// Piezas compartidas
// =====================================================================
@Composable
private fun Encabezado(titulo: String, subtitulo: String) {
    Column {
        Spacer(Modifier.height(24.dp))
        Text(titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)
        Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Cargando() {
    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

// Tarjeta de rendición para Finanzas. Si "seleccionada" no es null, muestra una casilla (pantalla de pago)
@Composable
private fun TarjetaFinanzas(r: RendicionFinanzas, onClick: () -> Unit, seleccionada: Boolean? = null) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada == true) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (seleccionada != null) {
                Checkbox(checked = seleccionada, onCheckedChange = { onClick() })
                Spacer(Modifier.width(8.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(r.rendicion.numero, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline)
                Text(r.rendicion.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${r.tecnico} · ${r.cantidadGastos} gastos", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatoClp(r.reembolso), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("a reembolsar", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FilaMonto(etiqueta: String, monto: Int, destacado: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, style = if (destacado) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal)
        Text(formatoClp(monto),
            style = if (destacado) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal,
            color = if (destacado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun MensajeErrorFinanzas(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Error, contentDescription = null,
            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(texto, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}
