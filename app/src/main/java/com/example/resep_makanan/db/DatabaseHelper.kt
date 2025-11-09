package com.example.resep_makanan.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.resep_makanan.R

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "resep_makanan.db"
        private const val DATABASE_VERSION = 5
        const val TABLE_RESEP = "resep"

        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_DESCRIPTION = "description"
        const val COL_IMAGE = "image"
        const val COL_INGREDIENTS_ID = "ingredients_id"
        const val COL_STEPS_ID = "steps_id"
        const val COL_TOOLS_ID = "tools_id"
        const val COL_CALORIES = "calories"
        const val COL_FIBER = "fiber"
        const val COL_PROTEIN = "protein"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE " + TABLE_RESEP + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_IMAGE + " INTEGER, " +
                COL_INGREDIENTS_ID + " INTEGER, " +
                COL_STEPS_ID + " INTEGER, " +
                COL_TOOLS_ID + " INTEGER, " +
                COL_CALORIES + " INTEGER, " +
                COL_FIBER + " INTEGER, " +
                COL_PROTEIN + " INTEGER)")
        db.execSQL(createTableQuery)
        addInitialRecipes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESEP")
        onCreate(db)
    }

    private fun addInitialRecipes(db: SQLiteDatabase) {
        addRecipe(db, "Nasi Goreng Spesial", "Nasi goreng klasik...", R.drawable.nasi_goreng, R.array.ingredients_nasi_goreng, R.array.steps_nasi_goreng, R.array.tools_nasi_goreng, 350, 4, 15)
        addRecipe(db, "Soto Ayam Lamongan", "Soto ayam dengan kuah kuning...", R.drawable.soto_ayam_lamongan, R.array.ingredients_soto_ayam, R.array.steps_soto_ayam, R.array.tools_soto_ayam, 400, 3, 25)
        addRecipe(db, "Gado-Gado Siram", "Salad sayuran khas Indonesia...", R.drawable.gado_gado_siram, R.array.ingredients_gado_gado, R.array.steps_gado_gado, R.array.tools_gado_gado, 300, 8, 12)
        addRecipe(db, "Rendang Daging", "Masakan daging sapi kaya rempah...", R.drawable.rendang_daging, R.array.ingredients_rendang, R.array.steps_rendang, R.array.tools_rendang, 468, 2, 41)
        addRecipe(db, "Sate Ayam Madura", "Sate ayam dengan bumbu kacang...", R.drawable.sate_ayam_madura, R.array.ingredients_sate_ayam, R.array.steps_sate_ayam, R.array.tools_sate_ayam, 380, 5, 30)
        addRecipe(db, "Bakso Sapi Kuah", "Bola daging sapi kenyal...", R.drawable.bakso_sapi_kuah, R.array.ingredients_bakso, R.array.steps_bakso, R.array.tools_bakso, 320, 2, 20)
        addRecipe(db, "Rawon Surabaya", "Sup daging berkuah hitam legam...", R.drawable.rawon_surabaya, R.array.ingredients_rawon, R.array.steps_rawon, R.array.tools_rawon, 450, 4, 35)
        addRecipe(db, "Pempek Palembang", "Kudapan lezat dari ikan dan sagu...", R.drawable.pempek_palembang, R.array.ingredients_pempek, R.array.steps_pempek, R.array.tools_pempek, 280, 1, 14)
        addRecipe(db, "Iga Bakar Madu", "Iga sapi empuk yang dibumbui...", R.drawable.iga_bakar_madu, R.array.ingredients_iga_bakar, R.array.steps_iga_bakar, R.array.tools_iga_bakar, 550, 1, 45)
        addRecipe(db, "Sayur Asem Jakarta", "Sayur-mayur segar dalam kuah asam...", R.drawable.sayur_asem_jakarta, R.array.ingredients_sayur_asem, R.array.steps_sayur_asem, R.array.tools_sayur_asem, 120, 7, 5)
    }

    private fun addRecipe(db: SQLiteDatabase, name: String, desc: String, image: Int, ingredientsResId: Int, stepsResId: Int, toolsResId: Int, cal: Int, fiber: Int, prot: Int) {
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_DESCRIPTION, desc)
            put(COL_IMAGE, image)
            put(COL_INGREDIENTS_ID, ingredientsResId)
            put(COL_STEPS_ID, stepsResId)
            put(COL_TOOLS_ID, toolsResId)
            put(COL_CALORIES, cal)
            put(COL_FIBER, fiber)
            put(COL_PROTEIN, prot)
        }
        db.insert(TABLE_RESEP, null, values)
    }
}