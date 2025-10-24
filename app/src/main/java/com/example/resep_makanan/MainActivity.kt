package com.example.resep_makanan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resep_makanan.adapter.ResepAdapter
import com.example.resep_makanan.db.DatabaseHelper
import com.example.resep_makanan.model.Resep

class MainActivity : AppCompatActivity() {

    private lateinit var rvResep: RecyclerView
    private val list = ArrayList<Resep>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Aneka Resep Lezat"

        rvResep = findViewById(R.id.rv_resep)
        rvResep.setHasFixedSize(true)

        list.addAll(getAllRecipes())
        showRecyclerList()
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
