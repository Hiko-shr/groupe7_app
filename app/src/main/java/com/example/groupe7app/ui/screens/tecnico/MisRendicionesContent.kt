package com.example.groupe7app.ui.screens.tecnico

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groupe7app.ui.components.EstadoVacio
import com.example.groupe7app.ui.components.TarjetaRendicion
import com.example.groupe7app.viewmodel.FiltroEstado
import com.example.groupe7app.viewmodel.MisRendicionesUiState

@Composable
fun MisRendicionesContent(
    estado: MisRendicionesUiState,
    onFiltro: (FiltroEstado) -> Unit,
    onAbrirRendicion: (Long) -> Unit,
    onNuevaRendicion: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaRendicion,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nueva rendición")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Mis rendiciones",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            // Fila de Filtros
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroEstado.entries.forEach { filtro ->
                    val seleccionado = estado.filtro == filtro
                    val colorFondo = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    val colorTexto = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colorFondo)
                            .clickable { onFiltro(filtro) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filtro.etiqueta,
                            color = colorTexto,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido de la lista
            if (estado.cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (estado.rendiciones.isEmpty()) {
                EstadoVacio(
                    mensaje = "No hay rendiciones para este filtro.",
                    textoAccion = "Crear rendición",
                    onAccion = onNuevaRendicion
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(estado.rendiciones) { resumen ->
                        TarjetaRendicion(
                            resumen = resumen,
                            onClick = { onAbrirRendicion(resumen.rendicion.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Espacio final para el FAB
                    }
                }
            }
        }
    }
}