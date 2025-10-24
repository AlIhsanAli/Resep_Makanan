package com.example.resep_makanan.model

import android.os.Parcelable
import androidx.annotation.ArrayRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Resep(
    val id: Int,
    val name: String,
    val description: String,
    val image: Int, // Drawable Resource ID
    @ArrayRes val ingredientsResId: Int, // Menggunakan String-Array Resource ID
    @ArrayRes val stepsResId: Int,       // Menggunakan String-Array Resource ID
    val calories: Int,
    val fiber: Int,
    val protein: Int
) : Parcelable
