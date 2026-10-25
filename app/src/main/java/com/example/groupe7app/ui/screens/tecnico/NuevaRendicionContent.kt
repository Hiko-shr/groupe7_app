package com.example.groupe7app.ui.screens.tecnico

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.groupe7app.model.enums.TipoRendicion
import com.example.groupe7app.ui.components.CampoValidado
import com.example.groupe7app.ui.components.formatoFecha
import com.example.groupe7app.viewmodel.NuevaRendicionUiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

// Diseño de la compañera, conectado al NuevaRendicionUiState.
// Solo dibuja: los datos vienen en "estado" y cada acción se avisa con un callback.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaRendicionContent(
    estado: NuevaRendicionUiState,
    onTituloChange: (String) -> Unit,
    onTipoChange: (TipoRendicion) -> Unit,
    onDestinoChange: (String) -> Unit,
    onDesdeChange: (LocalDate) -> Unit,
    onHastaChange: (LocalDate) -> Unit,
    onCentroCostoChange: (String) -> Unit,
    onObservacionChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    // Estado puramente visual: qué calendario está abierto ("desde", "hasta" o ninguno)
    var calendarioAbierto by remember { mutableStateOf<String?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Nueva rendición") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CampoValidado(
                valor = estado.titulo,
                onValorChange = onTituloChange,
                etiqueta = "Título (ej. Visita Cliente X)",
                error = estado.errores.titulo,
                contadorMax = 80
            )

            // ---------- Tipo de rendición (desplegable) ----------
            var expandidoTipo by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expandidoTipo, onExpandedChange = { expandidoTipo = it }) {
                OutlinedTextField(
                    value = estado.tipo.etiqueta,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de rendición") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoTipo) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandidoTipo, onDismissRequest = { expandidoTipo = false }) {
                    TipoRendicion.entries.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo.etiqueta) },
                            onClick = { onTipoChange(tipo); expandidoTipo = false }
                        )
                    }
                }
            }

            // El destino aparece solo si es comisión de servicio
            if (estado.tipo == TipoRendicion.COMISION_SERVICIO) {
                CampoValidado(
                    valor = estado.destino,
                    onValorChange = onDestinoChange,
                    etiqueta = "Ciudad de destino",
                    error = estado.errores.destino
                )
            }

            // ---------- Período (abre el calendario al tocar) ----------
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CampoFecha("Desde", estado.desde, estado.errores.periodo != null, Modifier.weight(1f)) {
                    calendarioAbierto = "desde"
                }
                CampoFecha("Hasta", estado.hasta, estado.errores.periodo != null, Modifier.weight(1f)) {
                    calendarioAbierto = "hasta"
                }
            }
            estado.errores.periodo?.let { MensajeError(it) }

            // ---------- Centro de costo (desplegable) ----------
            var expandidoCentro by remember { mutableStateOf(false) }
            val centroElegido = estado.centros.find { it.codigo == estado.centroCosto }
            ExposedDropdownMenuBox(expanded = expandidoCentro, onExpandedChange = { expandidoCentro = it }) {
                OutlinedTextField(
                    value = centroElegido?.let { "${it.codigo} · ${it.nombre}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Centro de costo") },
                    isError = estado.errores.centroCosto != null,
                    supportingText = { estado.errores.centroCosto?.let { MensajeError(it) } },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCentro) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandidoCentro, onDismissRequest = { expandidoCentro = false }) {
                    estado.centros.forEach { centro ->
                        DropdownMenuItem(
                            text = { Text("${centro.codigo} · ${centro.nombre}") },
                            onClick = { onCentroCostoChange(centro.codigo); expandidoCentro = false }
                        )
                    }
                }
            }

            CampoValidado(
                valor = estado.observacion,
                onValorChange = onObservacionChange,
                etiqueta = "Observación (opcional)",
                error = estado.errores.observacion,
                unaLinea = false,
                contadorMax = 200
            )

            Button(
                onClick = onGuardar,
                enabled = !estado.guardando,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (estado.guardando) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Crear rendición")
            }
        }
    }

    // ---------- Calendario (DatePicker de Material 3) ----------
    calendarioAbierto?.let { cual ->
        val fechaInicial = if (cual == "desde") estado.desde else estado.hasta
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaInicial?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { calendarioAbierto = null },
            confirmButton = {
                TextButton(onClick = {
                    // El DatePicker entrega milisegundos en UTC; los pasamos a LocalDate
                    pickerState.selectedDateMillis?.let { ms ->
                        val fecha = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate()
                        if (cual == "desde") onDesdeChange(fecha) else onHastaChange(fecha)
                    }
                    calendarioAbierto = null
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { calendarioAbierto = null }) { Text("Cancelar") } }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

// Campo de solo lectura que abre el calendario al tocarlo
@Composable
private fun CampoFecha(
    etiqueta: String,
    fecha: LocalDate?,
    conError: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    OutlinedTextField(
        value = fecha?.let { formatoFecha(it) } ?: "",
        onValueChange = {},
        readOnly = true,
        enabled = false,                       // deshabilitado para que el toque lo reciba el clickable
        label = { Text(etiqueta) },
        trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
        modifier = modifier.clickable(onClick = onClick),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = colores.onSurface,
            disabledBorderColor = if (conError) colores.error else colores.outline,
            disabledLabelColor = if (conError) colores.error else colores.onSurfaceVariant,
            disabledTrailingIconColor = colores.onSurfaceVariant
        )
    )
}

@Composable
private fun MensajeError(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Filled.Error, contentDescription = null,
            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(texto, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}
