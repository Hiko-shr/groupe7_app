package com.example.groupe7app.model.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Un solo lugar con las direcciones de los microservicios.
// 10.0.2.2 = "el computador" visto desde el emulador (localhost sería el propio emulador).
// En un teléfono real, cambia 10.0.2.2 por la IP de tu PC (ipconfig → IPv4).
object RetrofitClient {

    private const val HOST = "http://localhost"
    const val URL_USUARIOS = "$HOST:8081/"
    const val URL_RENDICIONES = "$HOST:8082/"

    // Muestra en Logcat cada petición y respuesta (filtra por "okhttp")
    private val cliente: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private fun retrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())     // JSON ↔ data class
            .build()

    val usuariosApi: UsuariosApi by lazy { retrofit(URL_USUARIOS).create(UsuariosApi::class.java) }
    val rendicionesApi: RendicionesApi by lazy { retrofit(URL_RENDICIONES).create(RendicionesApi::class.java) }
}
