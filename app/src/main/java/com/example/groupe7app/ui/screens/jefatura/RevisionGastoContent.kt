package com.example.groupe7app.ui.screens.jefatura

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.groupe7app.model.enums.MotivoRechazo
import com.example.groupe7app.ui.components.CampoValidado
import com.example.groupe7app.ui.components.ChipEstadoGasto
import com.example.groupe7app.ui.components.formatoClp
import com.example.groupe7app.ui.components.formatoFecha
import com.example.groupe7app.viewmodel.Decision
import com.example.groupe7app.viewmodel.RevisionGastoUiState
import java.io.File

// La jefatura revisa un gasto: ve el comprobante y sus datos, y decide aprobar, observar o rechazar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionGastoContent(
    estado: RevisionGastoUiState,
    onVolver: () -> Unit,
    onDecisionChange: (Decision) -> Unit,
    onMontoAprobadoChange: (String) -> Unit,
    onComentarioChange: (String) -> Unit,
    onMotivoChange: (MotivoRechazo) -> Unit,
    onGuardar: () -> Unit
) {
    val gasto = estado.gasto
    val errores = estado.errores

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Revisar gasto") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (gasto == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ---------- Comprobante ----------
            if (gasto.rutaFoto.isNotBlank()) {
                AsyncImage(
                    model = File(gasto.rutaFoto),
                    contentDescription = "Foto del comprobante",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(16.dp))
                )
            } else {
                Text("Este gasto no tiene foto del comprobante",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // ---------- Datos del gasto ----------
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
                        Text(estado.categoria, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        ChipEstadoGasto(estado = gasto.estado)
                    }
                    Dato("Técnico", estado.tecnico)
                    Dato("Proveedor", gasto.proveedor)
                    Dato("Fecha", formatoFecha(gasto.fechaDocumento))
                    Dato("Documento", gasto.tipoDocumento.etiqueta +
                            (gasto.folio?.let { " Nº $it" } ?: "") + (gasto.rutEmisor?.let { " · RUT $it" } ?: ""))
                    Dato("Medio de pago", gasto.medioPago.etiqueta)
                    Dato("Centro de costo", gasto.centroCosto)
                    Dato("Descripción", gasto.descripcion)
                    gasto.ordenTrabajo?.let { Dato("Orden de trabajo", it) }
                    HorizontalDivider()
                    Dato("Monto del comprobante", formatoClp(gasto.monto))
                    Dato("Admitido según tope", formatoClp(gasto.montoAdmitido))
                    if (gasto.latitud != null && gasto.longitud != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("%.5f, %.5f".format(gasto.latitud, gasto.longitud),
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Justificación del técnico si superó el tope
            gasto.justificacion?.let { NotaTecnico("Justificación del exceso", it) }
            // Respuesta del técnico a una observación anterior
            gasto.respuestaTecnico?.let { NotaTecnico("Respuesta a tu observación", it) }

            // ---------- Decisión ----------
            if (!estado.editable) {
                Text("Esta rendición ya no está en revisión.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Text("Tu decisión", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Decision.entries.forEach { d ->
                        FilterChip(
                            selected = estado.decision == d,
                            onClick = { onDecisionChange(d) },
                            label = { Text(d.etiqueta) }
                        )
                    }
                }
                errores.decision?.let { MensajeErrorRevision(it) }

                when (estado.decision) {
                    Decision.APROBAR -> CampoValidado(
                        valor = estado.montoAprobado,
                        onValorChange = onMontoAprobadoChange,
                        etiqueta = "Monto a aprobar",
                        error = errores.montoAprobado,
                        tipoTeclado = KeyboardType.Number,
                        prefijo = "$ "
                    )
                    Decision.OBSERVAR -> CampoValidado(
                        valor = estado.comentario,
                        onValorChange = onComentarioChange,
                        etiqueta = "¿Qué debe corregir el técnico?",
                        error = errores.comentario,
                        unaLinea = false,
                        contadorMax = 200
                    )
                    Decision.RECHAZAR -> {
                        SelectorMotivo(estado.motivo, errores.motivo, onMotivoChange)
                        CampoValidado(
                            valor = estado.comentario,
                            onValorChange = onComentarioChange,
                            etiqueta = if (estado.motivo == MotivoRechazo.OTRO) "Describe el motivo" else "Comentario (opcional)",
                            error = errores.comentario,
                            unaLinea = false,
                            contadorMax = 200
                        )
                    }
                    null -> {}
                }

                Button(
                    onClick = onGuardar,
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (estado.guardando) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("Guardar decisión")
                }
            }
        }
    }
}

@Composable
private fun Dato(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun NotaTecnico(titulo: String, texto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, fontWeight = FontWeight.Bold)
            Text(texto, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorMotivo(
    motivo: MotivoRechazo?,
    error: String?,
    onMotivoChange: (MotivoRechazo) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = motivo?.etiqueta ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Motivo del rechazo") },
            isError = error != null,
            supportingText = { error?.let { MensajeErrorRevision(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            MotivoRechazo.entries.forEach { m ->
                DropdownMenuItem(text = { Text(m.etiqueta) }, onClick = { onMotivoChange(m); expandido = false })
            }
        }
    }
}

@Composable
private fun MensajeErrorRevision(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Error, contentDescription = null,
            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(texto, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}
