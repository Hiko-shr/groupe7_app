package com.example.groupe7app.model.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Endpoints de ms-usuarios (puerto 8081). Retrofit escribe el código, igual que Room con los DAO.
interface UsuariosApi {

    @POST("api/usuarios/login")
    suspend fun login(@Body body: LoginRequest): UsuarioDto

    @GET("api/usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): UsuarioDto

    @GET("api/usuarios")
    suspend fun listar(@Query("jefaturaId") jefaturaId: Long? = null): List<UsuarioDto>
}
