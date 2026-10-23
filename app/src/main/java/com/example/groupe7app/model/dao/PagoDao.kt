package com.example.groupe7app.model.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.groupe7app.model.entity.Pago

@Dao
interface PagoDao {
    @Insert
    suspend fun insertar(pago: Pago): Long
}