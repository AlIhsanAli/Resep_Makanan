package com.example.resep_makanan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.resep_makanan.adapter.ResepAdapter
import com.example.resep_makanan.databinding.ActivityMainBinding
import com.example.resep_makanan.db.DatabaseHelper
import com.example.resep_makanan.model.Resep
import com.example.resep_makanan.model.WeatherResponse
import com.example.resep_makanan.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val list = ArrayList<Resep>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "" // Menghapus judul toolbar

        binding.rvResep.setHasFixedSize(true)

        list.addAll(getAllRecipes())
        showRecyclerList()
        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        RetrofitClient.instance.getWeather("Jakarta", "d2ab1a996f4ec3a8940469961b5484d9")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { weatherResponse ->
                            binding.tvCityName.text = "Jakarta"
                            binding.tvTemperature.text = "${weatherResponse.main.temp.toInt()}°"

                            val iconCode = weatherResponse.weather.firstOrNull()?.icon
                            val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"

                            Glide.with(this@MainActivity)
                                .load(iconUrl)
                                .into(binding.ivWeatherIcon)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("MainActivity", "Error: ${response.code()} - $errorBody")
                        binding.tvTemperature.text = "Err"
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("MainActivity", "Network failure", t)
                    binding.tvTemperature.text = "N/A"
                }
            })
    }

    private fun getAllRecipes(): ArrayList<Resep> {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val resepList = ArrayList<Resep>()

        val cursor = db.query(DatabaseHelper.TABLE_RESEP, null, null, null, null, null, "${DatabaseHelper.COL_ID} ASC")

        if (cursor.moveToFirst()) {
            do {
                val resep = Resep(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)),
                    image = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_IMAGE)),
                    ingredientsResId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INGREDIENTS_ID)),
                    stepsResId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STEPS_ID)),
                    calories = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CALORIES)),
                    fiber = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FIBER)),
                    protein = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROTEIN))
                )
                resepList.add(resep)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return resepList
    }

    private fun showRecyclerList() {
        binding.rvResep.layoutManager = LinearLayoutManager(this)
        val resepAdapter = ResepAdapter(list)
        binding.rvResep.adapter = resepAdapter

        resepAdapter.setOnItemClickCallback(object : ResepAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Resep, imageView: ImageView) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_RESEP, data)

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity, 
                    imageView, 
                    "resep_image"
                )
                
                startActivity(intent, options.toBundle())
            }
        })
    }
}
