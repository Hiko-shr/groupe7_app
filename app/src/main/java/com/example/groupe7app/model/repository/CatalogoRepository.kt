package com.example.groupe7app.model.repository

import com.example.groupe7app.model.dao.CategoriaDao
import com.example.groupe7app.model.dao.CentroCostoDao
import com.example.groupe7app.model.entity.Categoria
import com.example.groupe7app.model.entity.CentroCosto
import kotlinx.coroutines.flow.Flow

class CatalogoRepository(
    private val categoriaDao: CategoriaDao,
    private val centroCostoDao: CentroCostoDao
) {
    val categorias: Flow<List<Categoria>> = categoriaDao.todas()
    val centrosCosto: Flow<List<CentroCosto>> = centroCostoDao.todos()

    suspend fun categoria(id: Long): Categoria? = categoriaDao.obtener(id)
}