package com.example.resep_makanan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resep_makanan.adapter.ResepAdapter
import com.example.resep_makanan.db.DatabaseHelper
import com.example.resep_makanan.model.Resep
import com.example.resep_makanan.model.WeatherResponse
import com.example.resep_makanan.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var rvResep: RecyclerView
    private val list = ArrayList<Resep>()
    private lateinit var weatherTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Aneka Resep Lezat"

        rvResep = findViewById(R.id.rv_resep)
        rvResep.setHasFixedSize(true)
        weatherTextView = findViewById(R.id.weatherTextView)

        list.addAll(getAllRecipes())
        showRecyclerList()
        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        RetrofitClient.instance.getWeather("Jakarta", "d2ab1a996f4ec3a8940469961b5484d9")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        val temperature = weatherResponse?.main?.temp
                        val description = weatherResponse?.weather?.firstOrNull()?.description
                        weatherTextView.text = "Cuaca di Jakarta: $temperature°C, $description"
                    } else {
                        weatherTextView.text = "Gagal memuat cuaca"
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    weatherTextView.text = "Gagal memuat cuaca"
                    Log.e("MainActivity", "Error fetching weather data", t)
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
        rvResep.layoutManager = LinearLayoutManager(this)
        val resepAdapter = ResepAdapter(list)
        rvResep.adapter = resepAdapter

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
