package com.example.groupe7app.ui.screens.tecnico

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groupe7app.ui.components.TarjetaRendicion
import com.example.groupe7app.ui.components.formatoClp
import com.example.groupe7app.viewmodel.InicioTecnicoUiState

// Diseño de la compañera; solo se cambiaron los símbolos de texto por íconos de Material.
@Composable
fun InicioTecnicoContent(
    estado: InicioTecnicoUiState,
    onVerRendiciones: () -> Unit,
    onAbrirRendicion: (Long) -> Unit,
    onNuevoGasto: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoGasto,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nuevo gasto")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (estado.cargando) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // --- Cabecera ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hola, ${estado.nombre.substringBefore(" ")}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Técnico",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = estado.nombre.split(" ").take(2).joinToString("") { it.take(1) }.uppercase(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Avisos de Rendiciones Observadas ---
                estado.observadas.forEach { resumen ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onAbrirRendicion(resumen.rendicion.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Warning, contentDescription = "Observada")
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${resumen.rendicion.numero} observada", fontWeight = FontWeight.Bold)
                                Text(text = "${resumen.cantidadGastos} gasto(s) necesita(n) tu corrección", style = MaterialTheme.typography.bodySmall)
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = null)
                        }
                    }
                }

                if (estado.observadas.isNotEmpty()) Spacer(modifier = Modifier.height(12.dp))

                // --- Resumen del mes ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("RESUMEN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Text("tus rendiciones", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaResumen(titulo = "En borrador", monto = estado.resumen.enBorrador, modifier = Modifier.weight(1f))
                    TarjetaResumen(titulo = "En aprobación", monto = estado.resumen.enAprobacion, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaResumen(titulo = "Por reembolsar", monto = estado.resumen.porReembolsar, modifier = Modifier.weight(1f))
                    TarjetaResumen(titulo = "Reembolsado", monto = estado.resumen.reembolsado, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- Rendición en curso ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("RENDICIÓN EN CURSO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "Ver todas",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onVerRendiciones() }
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                if (estado.enCurso != null) {
                    TarjetaRendicion(
                        resumen = estado.enCurso,
                        onClick = { onAbrirRendicion(estado.enCurso.rendicion.id) }
                    )
                } else {
                    Text(
                        text = "No tienes ninguna rendición en curso en este momento.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun TarjetaResumen(titulo: String, monto: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = titulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = formatoClp(monto), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}