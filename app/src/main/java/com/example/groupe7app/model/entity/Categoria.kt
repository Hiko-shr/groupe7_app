package com.example.groupe7app.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val topePorGasto: Int? = null,
    val topeDiario: Int? = null,
    val permiteExceso: Boolean
)