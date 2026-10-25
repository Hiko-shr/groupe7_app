package com.example.groupe7app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CampoValidado(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    unaLinea: Boolean = true,
    prefijo: String? = null,
    contadorMax: Int? = null
) {
    val isError = error != null

    OutlinedTextField(
        value = valor,
        onValueChange = {
            if (contadorMax == null || it.length <= contadorMax) {
                onValorChange(it)
            }
        },
        label = { Text(etiqueta) },
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        singleLine = unaLinea,
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            imeAction = imeAction
        ),
        leadingIcon = icono?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        prefix = prefijo?.let {
            { Text(it) }
        },
        supportingText = {
            // Animación cuando aparece el error
            AnimatedVisibility(visible = isError) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "!",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (!isError && contadorMax != null) {
                Text(
                    text = "${valor.length} /$contadorMax",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}