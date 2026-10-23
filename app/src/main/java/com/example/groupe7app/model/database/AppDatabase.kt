package com.example.groupe7app.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.groupe7app.model.dao.CategoriaDao
import com.example.groupe7app.model.dao.CentroCostoDao
import com.example.groupe7app.model.dao.GastoDao
import com.example.groupe7app.model.dao.HistorialDao
import com.example.groupe7app.model.dao.PagoDao
import com.example.groupe7app.model.dao.RendicionDao
import com.example.groupe7app.model.dao.UsuarioDao
import com.example.groupe7app.model.entity.Categoria
import com.example.groupe7app.model.entity.CentroCosto
import com.example.groupe7app.model.entity.Gasto
import com.example.groupe7app.model.entity.HistorialEstado
import com.example.groupe7app.model.entity.Pago
import com.example.groupe7app.model.entity.Rendicion
import com.example.groupe7app.model.entity.Usuario

@Database(
    entities = [
        Usuario::class, CentroCosto::class, Categoria::class,
        Pago::class, Rendicion::class, Gasto::class, HistorialEstado::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun centroCostoDao(): CentroCostoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun rendicionDao(): RendicionDao
    abstract fun gastoDao(): GastoDao
    abstract fun historialDao(): HistorialDao
    abstract fun pagoDao(): PagoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "teqgastos.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}