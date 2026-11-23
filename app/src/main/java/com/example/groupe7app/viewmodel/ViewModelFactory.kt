package com.example.groupe7app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.groupe7app.model.database.AppDatabase
import com.example.groupe7app.model.preferences.SessionManager
import com.example.groupe7app.model.remote.RetrofitClient
import com.example.groupe7app.model.repository.CatalogoRepository
import com.example.groupe7app.model.repository.GastoRepository
import com.example.groupe7app.model.repository.RendicionRepository
import com.example.groupe7app.model.repository.UsuarioRepository

// Una sola fábrica para todos los ViewModels de la app (basada en ProductViewModelFactory del profesor)
class ViewModelFactory(private val app: Application) : ViewModelProvider.Factory {

    // "by lazy": se crea la primera vez que alguien lo usa, no antes
    private val db by lazy { AppDatabase.getInstance(app) }
    private val session by lazy { SessionManager(app) }

    // Los dos microservicios (Retrofit)
    private val usuariosApi by lazy { RetrofitClient.usuariosApi }
    private val rendicionesApi by lazy { RetrofitClient.rendicionesApi }

    // Cada repositorio recibe Room (la copia local) y la API (la fuente de verdad)
    private val usuarioRepo by lazy { UsuarioRepository(db.usuarioDao(), usuariosApi) }
    private val catalogoRepo by lazy { CatalogoRepository(db.categoriaDao(), db.centroCostoDao(), rendicionesApi) }
    private val rendicionRepo by lazy { RendicionRepository(db, rendicionesApi, usuariosApi) }
    private val gastoRepo by lazy { GastoRepository(db, rendicionesApi, usuariosApi) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm: ViewModel = when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(usuarioRepo, session)
            SesionViewModel::class.java -> SesionViewModel(session, usuarioRepo)
            // ----- Técnico -----
            InicioTecnicoViewModel::class.java -> InicioTecnicoViewModel(session, usuarioRepo, rendicionRepo, gastoRepo)
            MisRendicionesViewModel::class.java -> MisRendicionesViewModel(session, rendicionRepo, gastoRepo)
            NuevaRendicionViewModel::class.java -> NuevaRendicionViewModel(session, rendicionRepo, catalogoRepo)
            DetalleRendicionViewModel::class.java -> DetalleRendicionViewModel(session, rendicionRepo, gastoRepo, catalogoRepo)
            GastoViewModel::class.java -> GastoViewModel(session, usuarioRepo, rendicionRepo, gastoRepo, catalogoRepo)
            // ----- Jefatura -----
            BandejaJefaturaViewModel::class.java -> BandejaJefaturaViewModel(session, rendicionRepo, gastoRepo, usuarioRepo)
            RevisionRendicionViewModel::class.java -> RevisionRendicionViewModel(session, rendicionRepo, gastoRepo, catalogoRepo, usuarioRepo)
            RevisionGastoViewModel::class.java -> RevisionGastoViewModel(session, gastoRepo, rendicionRepo, catalogoRepo, usuarioRepo)
            // ----- Finanzas -----
            BandejaFinanzasViewModel::class.java -> BandejaFinanzasViewModel(rendicionRepo, gastoRepo, usuarioRepo)
            RevisionFinanzasViewModel::class.java -> RevisionFinanzasViewModel(session, rendicionRepo, gastoRepo, catalogoRepo, usuarioRepo)
            PagoViewModel::class.java -> PagoViewModel(session, rendicionRepo, gastoRepo, usuarioRepo)
            else -> throw IllegalArgumentException("ViewModel no registrado: ${modelClass.simpleName}")
        }
        return vm as T
    }
}
