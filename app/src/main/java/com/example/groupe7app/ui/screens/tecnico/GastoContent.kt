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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.groupe7app.ui.components.CampoValidado
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Data class local para asegurar la compilación
data class CategoriaGastoItem(
    val id: Long,
    val nombre: String
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoContent(
    monto: String,
    onMontoChange: (String) -> Unit,
    errorMonto: String?,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    errorDescripcion: String?,
    fecha: LocalDate?,
    onAbrirSelectorFecha: () -> Unit,
    categorias: List<CategoriaGastoItem>,
    categoriaSeleccionada: CategoriaGastoItem?,
    onCategoriaChange: (CategoriaGastoItem) -> Unit,
    proveedor: String,
    onProveedorChange: (String) -> Unit,
    numeroComprobante: String,
    onNumeroComprobanteChange: (String) -> Unit,
    onGuardarClick: () -> Unit,
    onVolverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formateador = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingresar gasto") },
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
                valor = monto,
                onValorChange = onMontoChange,
                etiqueta = "Monto ($)",
                error = errorMonto,
                tipoTeclado = KeyboardType.Number,
                prefijo = "$ "
            )

            CampoValidado(
                valor = descripcion,
                onValorChange = onDescripcionChange,
                etiqueta = "Descripción o motivo",
                error = errorDescripcion,
                contadorMax = 100
            )

            var expandidoCategoria by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandidoCategoria,
                onExpandedChange = { expandidoCategoria = it }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría de gasto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCategoria) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandidoCategoria,
                    onDismissRequest = { expandidoCategoria = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = {
                                onCategoriaChange(cat)
                                expandidoCategoria = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = fecha?.format(formateador) ?: "",
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Fecha del gasto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAbrirSelectorFecha() },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            CampoValidado(
                valor = proveedor,
                onValorChange = onProveedorChange,
                etiqueta = "Proveedor / Comercio",
                error = null
            )

            CampoValidado(
                valor = numeroComprobante,
                onValorChange = onNumeroComprobanteChange,
                etiqueta = "Nº Boleta o Factura",
                error = null
            )
            Button(
                onClick = onGuardarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar gasto")
            }
        }
    }
}