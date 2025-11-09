package com.example.resep_makanan.network

// Kelas utama yang membungkus semua respons
data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val dt: Long,
    val name: String
)

// Kelas untuk bagian "weather" dalam JSON (berupa array/list)
data class Weather(
    val description: String,
    val icon: String
)

// Kelas untuk bagian "main" dalam JSON
data class Main(
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int
)

// Kelas untuk bagian "wind" dalam JSON
data class Wind(
    val speed: Double
)
