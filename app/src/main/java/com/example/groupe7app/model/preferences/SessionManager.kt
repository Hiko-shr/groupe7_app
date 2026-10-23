package com.example.groupe7app.model.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Crea el archivo "sesion" donde DataStore guarda los valores (uno solo para toda la app)
private val Context.dataStore by preferencesDataStore(name = "sesion")

class SessionManager(private val context: Context) {

    // Las "claves" del diccionario: el nombre con que se guarda cada valor
    private object Claves {
        val USUARIO_ID = longPreferencesKey("usuario_id")
        val TEMA_OSCURO = booleanPreferencesKey("tema_oscuro")
    }

    // Id del usuario conectado; null si nadie ha iniciado sesión
    val usuarioId: Flow<Long?> = context.dataStore.data.map { it[Claves.USUARIO_ID] }

    // true = oscuro, false = claro, null = seguir el tema del teléfono
    val temaOscuro: Flow<Boolean?> = context.dataStore.data.map { it[Claves.TEMA_OSCURO] }

    suspend fun iniciarSesion(usuarioId: Long) {
        context.dataStore.edit { it[Claves.USUARIO_ID] = usuarioId }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { it.remove(Claves.USUARIO_ID) }
    }

    suspend fun setTemaOscuro(oscuro: Boolean) {
        context.dataStore.edit { it[Claves.TEMA_OSCURO] = oscuro }
    }
}