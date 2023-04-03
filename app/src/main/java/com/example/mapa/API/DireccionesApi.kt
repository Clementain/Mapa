package com.example.mapa.API

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://api.openrouteservice.org"
private const val INICIO = "8.681495,49.41461"
private const val FINAL = "8.687872,49.420318"
private const val API = "5b3ce3597851110001cf6248195446ce6bac45e7851606b557eab502"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit =
    Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi)).baseUrl(BASE_URL)
        .build()


interface DireccionesApi {
    @GET("v2/directionsdriving-car?api_key=${API}&start=${INICIO}&end=${FINAL}")
    suspend fun getDirections(): Coordenadas
}

object Direcciones {
    val retrofitService: DireccionesApi by lazy {
        retrofit.create(DireccionesApi::class.java)
    }
}