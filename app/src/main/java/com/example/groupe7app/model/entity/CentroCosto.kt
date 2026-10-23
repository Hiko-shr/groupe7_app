package com.example.groupe7app.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "centros_costo")
data class CentroCosto(
    @PrimaryKey val codigo: String,
    val nombre: String
)