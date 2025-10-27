package com.example.resep_makanan

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.resep_makanan.model.Resep
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RESEP = "extra_resep"
    }

    private var currentPortion = 1
    private var originalResep: Resep? = null

    private lateinit var tvPortionCount: TextView
    private lateinit var tvDetailIngredients: TextView
    private lateinit var tvDetailCalories: TextView
    private lateinit var tvDetailFiber: TextView
    private lateinit var tvDetailProtein: TextView
    private lateinit var tvDetailTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val resep = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_RESEP, Resep::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_RESEP)
        }

        originalResep = resep

        if (resep != null) {
            setupToolbar()
            bindViews()
            populateUi(resep)
            setupPortionButtons()
            setupFab()
        }
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.detail_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbar.title = " "
    }

    private fun bindViews() {
        tvPortionCount = findViewById(R.id.tv_portion_count)
        tvDetailIngredients = findViewById(R.id.tv_detail_ingredients)
        tvDetailCalories = findViewById(R.id.tv_detail_calories)
        tvDetailFiber = findViewById(R.id.tv_detail_fiber)
        tvDetailProtein = findViewById(R.id.tv_detail_protein)
        tvDetailTitle = findViewById(R.id.tv_detail_title)
    }

    private fun populateUi(resep: Resep) {
        tvDetailTitle.text = resep.name
        findViewById<ImageView>(R.id.iv_detail_image).setImageResource(resep.image)

        val stepsArray = resources.getStringArray(resep.stepsResId)
        val stepsTextView: TextView = findViewById(R.id.tv_detail_steps)
        stepsTextView.text = stepsArray
            .mapIndexed { index, step -> "${index + 1}. $step" }
            .joinToString("\n\n")

        updateDynamicContent()
    }

    private fun setupPortionButtons() {
        findViewById<Button>(R.id.btn_increase_portion).setOnClickListener {
            currentPortion++
            updateDynamicContent()
        }

        findViewById<Button>(R.id.btn_decrease_portion).setOnClickListener {
            if (currentPortion > 1) {
                currentPortion--
                updateDynamicContent()
            }
        }
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.fab_timer).setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateDynamicContent() {
        tvPortionCount.text = currentPortion.toString()

        originalResep?.let { resep ->
            tvDetailCalories.text = "${resep.calories * currentPortion} kcal"
            tvDetailFiber.text = "${resep.fiber * currentPortion} g"
            tvDetailProtein.text = "${resep.protein * currentPortion} g"

            val ingredientsArray = resources.getStringArray(resep.ingredientsResId)
            tvDetailIngredients.text = ingredientsArray.joinToString(separator = "\n") { ingredientString ->
                val parts = ingredientString.split(";")
                val name = parts.getOrNull(0) ?: ""
                val amountStr = parts.getOrNull(1)?.trim()
                val unit = parts.getOrNull(2) ?: ""

                var newAmount = ""
                amountStr?.toDoubleOrNull()?.let { amount ->
                    if (amount > 0) {
                        val calculatedAmount = amount * currentPortion
                        newAmount = if (calculatedAmount % 1 == 0.0) {
                            calculatedAmount.toInt().toString()
                        } else {
                            String.format(Locale.US, "%.1f", calculatedAmount)
                        }
                    }
                }

                if (newAmount.isNotEmpty()) {
                    "• $name ($newAmount $unit)"
                } else {
                    "• $name ($unit)"
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
