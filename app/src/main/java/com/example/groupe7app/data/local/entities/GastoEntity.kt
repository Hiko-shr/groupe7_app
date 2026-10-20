package com.example.groupe7app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "gastos",
    foreignKeys = [
        ForeignKey(
            entity = RendicionEntity::class,
            parentColumns = ["id"],
            childColumns = ["rendicionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GastoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rendicionId: Long,
    val categoria: String,      // Combustible, Peaje, Alimentación, Alojamiento, etc.
    val fecha: String,          // DD-MM-YYYY
    val tipoDocumento: String,  // Boleta, Factura, Ticket
    val rutEmisor: String,
    val folio: String,
    val proveedor: String,
    val monto: Double,
    val medioPago: String,      // Tarjeta corporativa, Fondo por rendir, Dinero propio
    val centroCosto: String,
    val descripcion: String,
    val ordenTrabajo: String? = null,
    val fotoPath: String,       // Ruta local donde se guardó la foto
    val latitud: Double? = null,
    val longitud: Double? = null,
    val estadoGasto: String = "PENDIENTE" // PENDIENTE, APROBADO, OBSERVADO, RECHAZADO
)