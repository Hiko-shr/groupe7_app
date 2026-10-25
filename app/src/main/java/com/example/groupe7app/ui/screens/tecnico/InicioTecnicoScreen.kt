package com.example.groupe7app.ui.screens.tecnico

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.groupe7app.viewmodel.InicioTecnicoViewModel

// Conecta el ViewModel con el diseño (InicioTecnicoContent)
@Composable
fun InicioTecnicoScreen(
    viewModel: InicioTecnicoViewModel,
    onVerRendiciones: () -> Unit,
    onAbrirRendicion: (Long) -> Unit,
    onNuevaRendicion: () -> Unit,
    onNuevoGasto: (Long) -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    InicioTecnicoContent(
        estado = estado,
        onVerRendiciones = onVerRendiciones,
        onAbrirRendicion = onAbrirRendicion,
        // "Nuevo gasto": si hay una rendición en borrador se agrega ahí; si no, primero se crea una
        onNuevoGasto = {
            val enCurso = estado.enCurso
            if (enCurso != null) onNuevoGasto(enCurso.rendicion.id) else onNuevaRendicion()
        }
    )
}
