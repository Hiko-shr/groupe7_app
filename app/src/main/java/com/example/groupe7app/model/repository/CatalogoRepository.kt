package com.example.groupe7app.model.repository

import com.example.groupe7app.model.dao.CategoriaDao
import com.example.groupe7app.model.dao.CentroCostoDao
import com.example.groupe7app.model.entity.Categoria
import com.example.groupe7app.model.entity.CentroCosto
import com.example.groupe7app.model.remote.RendicionesApi
import com.example.groupe7app.model.remote.aEntidad
import kotlinx.coroutines.flow.Flow

class CatalogoRepository(
    private val categoriaDao: CategoriaDao,
    private val centroCostoDao: CentroCostoDao,
    private val api: RendicionesApi
) {
    val categorias: Flow<List<Categoria>> = categoriaDao.todas().refrescandoCon { sincronizarCategorias() }

    val centrosCosto: Flow<List<CentroCosto>> = centroCostoDao.todos().refrescandoCon {
        val centros = api.centrosCosto().map { it.aEntidad() }
        centroCostoDao.eliminarExcepto(centros.map { it.codigo })
        centroCostoDao.guardarTodos(centros)
    }

    // Primero busca en Room; si no está, descarga las categorías y vuelve a buscar
    suspend fun categoria(id: Long): Categoria? {
        categoriaDao.obtener(id)?.let { return it }
        intentar { sincronizarCategorias() }
        return categoriaDao.obtener(id)
    }

    private suspend fun sincronizarCategorias() {
        val categorias = api.categorias().map { it.aEntidad() }
        categoriaDao.eliminarExcepto(categorias.map { it.id })
        categoriaDao.guardarTodas(categorias)
    }
}
