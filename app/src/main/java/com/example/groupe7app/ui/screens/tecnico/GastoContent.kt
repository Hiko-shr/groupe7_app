package com.example.groupe7app.ui.screens.tecnico

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Receipt
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
import com.example.groupe7app.model.entity.Categoria
import com.example.groupe7app.model.enums.MedioPago
import com.example.groupe7app.model.enums.TipoDocumento
import com.example.groupe7app.ui.components.CampoValidado
import com.example.groupe7app.ui.components.TarjetaTope
import com.example.groupe7app.ui.components.formatoFecha
import com.example.groupe7app.viewmodel.GastoUiState
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

// Formulario de gasto con el estilo de la compañera (TopAppBar, columna con CampoValidado y desplegables).
// Solo dibuja: los datos vienen en "estado" y cada acción se avisa con un callback.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoContent(
    estado: GastoUiState,
    onVolver: () -> Unit,
    onTomarFoto: () -> Unit,
    onElegirGaleria: () -> Unit,
    onCategoriaChange: (Categoria) -> Unit,
    onFechaChange: (LocalDate) -> Unit,
    onTipoDocumentoChange: (TipoDocumento) -> Unit,
    onRutChange: (String) -> Unit,
    onFolioChange: (String) -> Unit,
    onProveedorChange: (String) -> Unit,
    onMontoChange: (String) -> Unit,
    onJustificacionChange: (String) -> Unit,
    onMedioPagoChange: (MedioPago) -> Unit,
    onCentroCostoChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onOrdenTrabajoChange: (String) -> Unit,
    onRespuestaChange: (String) -> Unit,
    onAgregarUbicacion: () -> Unit,
    onGuardar: () -> Unit
) {
    val errores = estado.errores
    var calendarioAbierto by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(if (estado.gastoId == null) "Ingresar gasto" else "Editar gasto") },
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
            // ---------- Aviso si ya no se puede modificar ----------
            if (!estado.editable) {
                Aviso("Este gasto ya está en revisión o aprobado: no se puede modificar.")
            }

            // ---------- Observación de la jefatura ----------
            estado.comentarioJefatura?.let { comentario ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Observación de la jefatura", fontWeight = FontWeight.Bold)
                        Text(comentario, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                CampoValidado(
                    valor = estado.respuesta,
                    onValorChange = onRespuestaChange,
                    etiqueta = "Tu respuesta a la observación",
                    error = errores.respuesta,
                    unaLinea = false,
                    contadorMax = 200
                )
            }

            // ---------- Foto del comprobante (cámara o galería) ----------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (estado.fotoUri != null) {
                        AsyncImage(
                            model = File(estado.fotoUri),
                            contentDescription = "Foto del comprobante",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Receipt, contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Fotografía la boleta o factura",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onTomarFoto, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Cámara")
                        }
                        OutlinedButton(onClick = onElegirGaleria, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Galería")
                        }
                    }
                    errores.foto?.let { MensajeErrorGasto(it) }
                }
            }

            // ---------- Categoría ----------
            Desplegable(
                etiqueta = "Categoría de gasto",
                opciones = estado.categorias,
                seleccionado = estado.categoria,
                texto = { it.nombre },
                error = errores.categoria,
                onElegir = onCategoriaChange
            )

            // ---------- Fecha del documento ----------
            OutlinedTextField(
                value = formatoFecha(estado.fechaDocumento),
                onValueChange = {},
                readOnly = true,
                enabled = false,                       // deshabilitado para que el toque lo reciba el clickable
                label = { Text("Fecha del documento") },
                trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                isError = errores.fecha != null,
                supportingText = { errores.fecha?.let { MensajeErrorGasto(it) } },
                modifier = Modifier.fillMaxWidth().clickable { calendarioAbierto = true },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (errores.fecha != null) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSupportingTextColor = MaterialTheme.colorScheme.error
                )
            )

            // ---------- Tipo de documento ----------
            Text("Tipo de documento", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoDocumento.entries.forEach { tipo ->
                    FilterChip(
                        selected = estado.tipoDocumento == tipo,
                        onClick = { onTipoDocumentoChange(tipo) },
                        label = { Text(tipo.etiqueta) }
                    )
                }
            }

            // RUT y folio solo para boleta y factura (el ticket no los trae)
            if (estado.tipoDocumento != TipoDocumento.TICKET) {
                CampoValidado(
                    valor = estado.rutEmisor,
                    onValorChange = onRutChange,
                    etiqueta = "RUT del emisor (ej. 76.543.210-3)",
                    error = errores.rut
                )
                CampoValidado(
                    valor = estado.folio,
                    onValorChange = onFolioChange,
                    etiqueta = "Nº de boleta o factura (folio)",
                    error = errores.folio,
                    tipoTeclado = KeyboardType.Number
                )
            }

            CampoValidado(
                valor = estado.proveedor,
                onValorChange = onProveedorChange,
                etiqueta = "Proveedor / Comercio",
                error = errores.proveedor,
                contadorMax = 80
            )

            CampoValidado(
                valor = estado.monto,
                onValorChange = onMontoChange,
                etiqueta = "Monto",
                error = errores.monto,
                tipoTeclado = KeyboardType.Number,
                prefijo = "$ "
            )

            // ---------- Tope de la categoría (RN-01) ----------
            val tope = estado.tope
            val categoria = estado.categoria
            if (tope != null && categoria != null) {
                TarjetaTope(tope = tope, nombreCategoria = categoria.nombre)
            }
            if (tope?.requiereJustificacion == true) {
                CampoValidado(
                    valor = estado.justificacion,
                    onValorChange = onJustificacionChange,
                    etiqueta = "Justificación del exceso",
                    error = errores.justificacion,
                    unaLinea = false,
                    contadorMax = 200
                )
            }

            // ---------- Medio de pago y centro de costo ----------
            Desplegable(
                etiqueta = "Medio de pago",
                // La tarjeta corporativa solo aparece si el técnico tiene una asignada
                opciones = MedioPago.entries.filter { it != MedioPago.TARJETA_CORPORATIVA || estado.tieneTarjeta },
                seleccionado = estado.medioPago,
                texto = { it.etiqueta },
                error = errores.medioPago,
                onElegir = onMedioPagoChange
            )

            Desplegable(
                etiqueta = "Centro de costo",
                opciones = estado.centros,
                seleccionado = estado.centros.find { it.codigo == estado.centroCosto },
                texto = { "${it.codigo} · ${it.nombre}" },
                error = errores.centroCosto,
                onElegir = { onCentroCostoChange(it.codigo) }
            )

            CampoValidado(
                valor = estado.descripcion,
                onValorChange = onDescripcionChange,
                etiqueta = "Descripción o motivo",
                error = errores.descripcion,
                unaLinea = false,
                contadorMax = 200
            )

            CampoValidado(
                valor = estado.ordenTrabajo,
                onValorChange = onOrdenTrabajoChange,
                etiqueta = "Orden de trabajo (opcional)",
                error = null
            )

            // ---------- Ubicación (GPS) ----------
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onAgregarUbicacion, enabled = !estado.buscandoUbicacion) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (estado.latitud == null) "Agregar ubicación" else "Actualizar ubicación")
                }
                Spacer(Modifier.width(12.dp))
                when {
                    estado.buscandoUbicacion -> CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    estado.latitud != null && estado.longitud != null -> Text(
                        "%.5f, %.5f".format(estado.latitud, estado.longitud),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            estado.mensajeUbicacion?.let { MensajeErrorGasto(it) }

            // ---------- Guardar ----------
            if (estado.editable) {
                Button(
                    onClick = onGuardar,
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (estado.guardando) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("Guardar gasto")
                }
            }
        }
    }

    // ---------- Calendario ----------
    if (calendarioAbierto) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = estado.fechaDocumento.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { calendarioAbierto = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { ms ->
                        onFechaChange(Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    calendarioAbierto = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { calendarioAbierto = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

// Desplegable genérico (sirve para categoría, medio de pago y centro de costo).
// <T> significa "cualquier tipo": la misma función funciona con Categoria, MedioPago o CentroCosto.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> Desplegable(
    etiqueta: String,
    opciones: List<T>,
    seleccionado: T?,
    texto: (T) -> String,
    error: String?,
    onElegir: (T) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = seleccionado?.let(texto) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            isError = error != null,
            supportingText = { error?.let { MensajeErrorGasto(it) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(texto(opcion)) },
                    onClick = { onElegir(opcion); expandido = false }
                )
            }
        }
    }
}

@Composable
private fun Aviso(texto: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}

@Composable
private fun MensajeErrorGasto(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Error, contentDescription = null,
            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(texto, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}
