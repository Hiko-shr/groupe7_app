package com.example.groupe7app.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.groupe7app.model.enums.Rol

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val clave: String,
    val rol: Rol,
    val jefaturaId: Long? = null,
    val tieneTarjeta: Boolean = false
)