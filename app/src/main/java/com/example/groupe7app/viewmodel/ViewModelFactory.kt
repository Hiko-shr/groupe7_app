package com.example.groupe7app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.groupe7app.model.database.AppDatabase
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.repository.CatalogoRepository
import com.example.groupe7app.model.repository.GastoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import com.example.groupe7app.model.repository.UsuarioRepository

// Una sola fábrica para todos los ViewModels de la app (basada en ProductViewModelFactory del profesor)
class ViewModelFactory(private val app: Application) : ViewModelProvider.Factory {

    // "by lazy": se crea la primera vez que alguien lo usa, no antes
    private val db by lazy { AppDatabase.getInstance(app) }
    private val session by lazy { SessionManager(app) }
    private val usuarioRepo by lazy { UsuarioRepository(db.usuarioDao()) }
    private val catalogoRepo by lazy { CatalogoRepository(db.categoriaDao(), db.centroCostoDao()) }
    private val rendicionRepo by lazy { RendicionRepository(db) }
    private val gastoRepo by lazy { GastoRepository(db) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm: ViewModel = when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(usuarioRepo, session)
            SesionViewModel::class.java -> SesionViewModel(session, usuarioRepo)
            else -> throw IllegalArgumentException("ViewModel no registrado: ${modelClass.simpleName}")
        }
        return vm as T
    }
}