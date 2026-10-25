package com.example.groupe7app.ui.components
import com.example.groupe7app.model.rules.ResultadoTope
// (Nota: Si tu archivo ResultadoTope.kt lo guardaste en otra carpeta como viewmodel, la ruta sería com.example.groupe7app.viewmodel.ResultadoTope)
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TarjetaTope(
    tope: ResultadoTope,
    nombreCategoria: String,
    modifier: Modifier = Modifier
) {
    // Si supera el tope, usamos color tertiaryContainer; si no, surfaceVariant
    val colorFondo = if (tope.superaTope) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val colorTexto = if (tope.superaTope) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Tope de $nombreCategoria: ${if(tope.esDiario) "diario" else "por gasto"}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colorTexto
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Mostrar disponible
            if (tope.disponible != null) {
                Text(
                    text = "Disponible previo a este gasto: ${formatoClp(tope.disponible)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorTexto
                )
            }

            // Mostrar monto admitido
            Text(
                text = "Monto admitido: ${formatoClp(tope.admitido)}",
                style = MaterialTheme.typography.bodySmall,
                color = colorTexto
            )

            // Si hay exceso, mostrarlo claramente
            if (tope.superaTope) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    // Si la categoría permite exceso, se admite con justificación; si no, ese monto no se reembolsa
                    text = if (tope.permiteExceso) "Supera el tope por ${formatoClp(tope.exceso)}: debes justificarlo"
                    else "Exceso no admitido: ${formatoClp(tope.exceso)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error // El exceso siempre en color error para destacar
                )
            }
        }
    }
}