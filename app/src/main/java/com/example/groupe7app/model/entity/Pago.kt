package com.example.groupe7app.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "pagos")
data class Pago(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fecha: LocalDate,
    val numeroOperacion: String,      // n.º de la transferencia
    val monto: Int,
    val observacion: String? = null,
    val usuarioFinanzasId: Long       // quién registró el pago
)