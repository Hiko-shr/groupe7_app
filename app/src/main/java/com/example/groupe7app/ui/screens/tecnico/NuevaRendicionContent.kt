package com.example.groupe7app.ui.screens.tecnico

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.groupe7app.model.entity.CentroCosto
import com.example.groupe7app.model.enums.TipoRendicion
import com.example.groupe7app.ui.components.CampoValidado
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaRendicionContent(
    titulo: String,
    onTituloChange: (String) -> Unit,
    errorTitulo: String?,
    tipoSeleccionado: TipoRendicion?,
    onTipoChange: (TipoRendicion) -> Unit,
    destino: String,
    onDestinoChange: (String) -> Unit,
    fechaDesde: LocalDate?,
    fechaHasta: LocalDate?,
    onAbrirSelectorFecha: (Boolean) -> Unit,
    centrosCosto: List<CentroCosto>,
    centroSeleccionado: CentroCosto?,
    onCentroChange: (CentroCosto) -> Unit,
    onGuardarClick: () -> Unit,
    onVolverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formateador = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva rendición") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Text("<", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CampoValidado(
                valor = titulo,
                onValorChange = onTituloChange,
                etiqueta = "Título (ej. Visita Cliente X)",
                error = errorTitulo,
                contadorMax = 50
            )

            var expandidoTipo by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandidoTipo,
                onExpandedChange = { expandidoTipo = it }
            ) {
                OutlinedTextField(
                    value = tipoSeleccionado?.etiqueta ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de rendición") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandidoTipo,
                    onDismissRequest = { expandidoTipo = false }
                ) {
                    TipoRendicion.entries.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo.etiqueta) },
                            onClick = {
                                onTipoChange(tipo)
                                expandidoTipo = false
                            }
                        )
                    }
                }
            }

            if (tipoSeleccionado == TipoRendicion.COMISION_SERVICIO) {
                CampoValidado(
                    valor = destino,
                    onValorChange = onDestinoChange,
                    etiqueta = "Ciudad de destino",
                    error = null
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = fechaDesde?.format(formateador) ?: "",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Desde") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAbrirSelectorFecha(true) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                OutlinedTextField(
                    value = fechaHasta?.format(formateador) ?: "",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Hasta") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAbrirSelectorFecha(false) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            var expandidoCentro by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandidoCentro,
                onExpandedChange = { expandidoCentro = it }
            ) {
                OutlinedTextField(
                    value = centroSeleccionado?.let { "${it.codigo} - ${it.nombre}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Centro de costo") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandidoCentro,
                    onDismissRequest = { expandidoCentro = false }
                ) {
                    centrosCosto.forEach { centro ->
                        DropdownMenuItem(
                            text = { Text("${centro.codigo} - ${centro.nombre}") },
                            onClick = {
                                onCentroChange(centro)
                                expandidoCentro = false
                            }
                        )
                    }
                }
            }
            Button(
                onClick = onGuardarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Crear rendición")
            }
        }
    }
}