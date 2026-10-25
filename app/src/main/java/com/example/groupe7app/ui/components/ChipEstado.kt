package com.example.groupe7app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.groupe7app.model.enums.EstadoGasto
import com.example.groupe7app.model.enums.EstadoRendicion

@Composable
fun ChipEstado(estado: EstadoRendicion, modifier: Modifier = Modifier) {
    val (colorFondo, colorTexto, texto) = when (estado) {
        EstadoRendicion.BORRADOR -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "Borrador")
        EstadoRendicion.ENVIADA -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, "En aprobación")
        EstadoRendicion.OBSERVADA -> Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, "Observada")
        EstadoRendicion.APROBADA_JEFATURA -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "Aprobada por jefatura")
        EstadoRendicion.APROBADA_FINANZAS -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "Aprobada")
        EstadoRendicion.PAGADA -> Triple(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary, "Pagada")
        EstadoRendicion.RECHAZADA -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "Rechazada")
    }

    ChipBase(texto = texto, colorFondo = colorFondo, colorTexto = colorTexto, modifier = modifier)
}

@Composable
fun ChipEstadoGasto(estado: EstadoGasto, modifier: Modifier = Modifier) {
    val (colorFondo, colorTexto, texto) = when (estado) {
        EstadoGasto.BORRADOR -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "Borrador")
        EstadoGasto.ENVIADO -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, "En revisión")
        EstadoGasto.OBSERVADO -> Triple(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, "Observado")
        EstadoGasto.APROBADO -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "Aprobado")
        EstadoGasto.APROBADO_FINAL -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "Aprobado final")
        EstadoGasto.RECHAZADO -> Triple(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "Rechazado")
    }

    ChipBase(texto = texto, colorFondo = colorFondo, colorTexto = colorTexto, modifier = modifier)
}

@Composable
private fun ChipBase(texto: String, colorFondo: Color, colorTexto: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50)) // Forma de píldora
            .background(colorFondo)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = colorTexto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}