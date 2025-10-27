package com.example.resep_makanan.network

import com.example.resep_makanan.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric" // Menambahkan parameter units
    ): Call<WeatherResponse>
}
