package com.example.resep_makanan

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.resep_makanan.network.RetrofitClient
import com.example.resep_makanan.network.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CuacaActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var etSearch: EditText
    private lateinit var ivSearch: ImageView
    private lateinit var tvCity: TextView
    private lateinit var tvDate: TextView
    private lateinit var ivWeatherIcon: ImageView
    private lateinit var tvTemperature: TextView
    private lateinit var tvWeatherDescription: TextView
    private lateinit var tvFeelsLike: TextView
    private lateinit var tvPressure: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvWind: TextView

    private val apiKey = "d2ab1a996f4ec3a8940469961b5484d9"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuaca)

        ivBack = findViewById(R.id.ivBack)
        etSearch = findViewById(R.id.etSearch)
        ivSearch = findViewById(R.id.ivSearch)
        tvCity = findViewById(R.id.tvCity)
        tvDate = findViewById(R.id.tvDate)
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvWeatherDescription = findViewById(R.id.tvWeatherDescription)
        tvFeelsLike = findViewById(R.id.tvFeelsLike)
        tvPressure = findViewById(R.id.tvPressure)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvWind = findViewById(R.id.tvWind)

        ivBack.setOnClickListener {
            finish()
        }

        ivSearch.setOnClickListener {
            performSearch()
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        //  kota default
        getWeatherData("Yogyakarta")
    }

    private fun performSearch() {
        val city = etSearch.text.toString()
        if (city.isNotEmpty()) {
            getWeatherData(city)
        } else {
            Toast.makeText(this, "Masukkan nama kota", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getWeatherData(city: String) {
        RetrofitClient.instance.getWeather(city, apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        updateUI(it)
                    }
                } else {
                    Toast.makeText(this@CuacaActivity, "Kota tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@CuacaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(weather: WeatherResponse) {
        tvCity.text = weather.name
        tvDate.text = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date(weather.dt * 1000L))
        tvTemperature.text = "${weather.main.temp.toInt()}°"
        tvWeatherDescription.text = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: ""
        tvFeelsLike.text = "${weather.main.feels_like.toInt()}°"
        tvPressure.text = "${weather.main.pressure} hPa"
        tvHumidity.text = "${weather.main.humidity}%"
        tvWind.text = "${String.format("%.1f", weather.wind.speed * 3.6)} km/h"

        val iconUrl = "https://openweathermap.org/img/wn/${weather.weather.firstOrNull()?.icon}@2x.png"
        Glide.with(this).load(iconUrl).into(ivWeatherIcon)
    }
}