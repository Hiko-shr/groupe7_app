package com.example.groupe7app.ui.navigation

// Todas las pantallas de la app (igual que Screen.kt del ejemplo test_mvvm_duoc del profesor)
sealed class Screen(val route: String) {

    // ----- Técnico -----
    data object TecInicio : Screen("tecnico/inicio")
    data object TecRendiciones : Screen("tecnico/rendiciones")
    data object TecNuevaRendicion : Screen("tecnico/rendicion/nueva")
    data object TecDetalle : Screen("tecnico/rendicion/{rendicionId}") {
        fun crear(rendicionId: Long) = "tecnico/rendicion/$rendicionId"
    }
    data object TecGasto : Screen("tecnico/rendicion/{rendicionId}/gasto") {
        fun crear(rendicionId: Long) = "tecnico/rendicion/$rendicionId/gasto"
    }

    // ----- Jefatura -----
    data object JefBandeja : Screen("jefatura/bandeja")
    data object JefRevision : Screen("jefatura/revision/{rendicionId}") {
        fun crear(rendicionId: Long) = "jefatura/revision/$rendicionId"
    }

    // ----- Finanzas -----
    data object FinBandeja : Screen("finanzas/bandeja")
    data object FinRevision : Screen("finanzas/revision/{rendicionId}") {
        fun crear(rendicionId: Long) = "finanzas/revision/$rendicionId"
    }
    data object FinPago : Screen("finanzas/pago")

    // ----- Comunes -----
    data object Perfil : Screen("perfil")
}