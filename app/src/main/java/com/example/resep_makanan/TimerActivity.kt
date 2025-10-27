package com.example.resep_makanan

import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.util.Locale

class TimerActivity : AppCompatActivity() {

    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var isTimerRunning = false

    private lateinit var tvTimerDisplay: TextView
    private lateinit var pickerContainer: LinearLayout
    private lateinit var pickerHours: NumberPicker
    private lateinit var pickerMinutes: NumberPicker
    private lateinit var pickerSeconds: NumberPicker
    private lateinit var btnStartPause: Button
    private lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        
        val toolbar: Toolbar = findViewById(R.id.timer_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Timer Memasak"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bindViews()
        setupPickers()
        setupButtons()
    }

    private fun bindViews() {
        tvTimerDisplay = findViewById(R.id.tv_timer_display)
        pickerContainer = findViewById(R.id.ll_picker_container)
        pickerHours = findViewById(R.id.picker_hours)
        pickerMinutes = findViewById(R.id.picker_minutes)
        pickerSeconds = findViewById(R.id.picker_seconds)
        btnStartPause = findViewById(R.id.btn_start_pause)
        btnReset = findViewById(R.id.btn_reset)
    }

    private fun setupPickers() {
        pickerHours.minValue = 0
        pickerHours.maxValue = 23
        pickerMinutes.minValue = 0
        pickerMinutes.maxValue = 59
        pickerSeconds.minValue = 0
        pickerSeconds.maxValue = 59
    }

    private fun setupButtons() {
        btnStartPause.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        btnReset.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {
        // Jika timer sudah berjalan, jangan lakukan apa-apa
        if (isTimerRunning && timeLeftInMillis > 0) return
        
        // Jika di-pause, lanjutkan dari sisa waktu
        val duration = if (timeLeftInMillis > 0) {
            timeLeftInMillis
        } else {
            val hours = pickerHours.value
            val minutes = pickerMinutes.value
            val seconds = pickerSeconds.value
            (hours * 3600 + minutes * 60 + seconds) * 1000L
        }

        if (duration == 0L) return

        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay()
            }

            override fun onFinish() {
                isTimerRunning = false
                btnStartPause.text = "Mulai"
                btnReset.visibility = View.VISIBLE
                pickerContainer.visibility = View.VISIBLE
                notifyFinished()
            }
        }.start()

        isTimerRunning = true
        btnStartPause.text = "Jeda"
        btnReset.visibility = View.INVISIBLE
        pickerContainer.visibility = View.INVISIBLE
    }

    private fun pauseTimer() {
        timer?.cancel()
        isTimerRunning = false
        btnStartPause.text = "Lanjut"
        btnReset.visibility = View.VISIBLE
    }

    private fun resetTimer() {
        timer?.cancel()
        timeLeftInMillis = 0
        isTimerRunning = false
        updateTimerDisplay()
        btnStartPause.text = "Mulai"
        btnStartPause.isEnabled = true
        btnReset.visibility = View.VISIBLE
        pickerContainer.visibility = View.VISIBLE
    }

    private fun updateTimerDisplay() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        tvTimerDisplay.text = timeFormatted
    }

    private fun notifyFinished() {
        try {
            // Sound
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()

            // Vibration
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(500)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
