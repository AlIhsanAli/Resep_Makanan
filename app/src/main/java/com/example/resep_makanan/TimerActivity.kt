package com.example.resep_makanan

import android.content.Context
import android.media.Ringtone
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
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.util.Locale

class TimerActivity : AppCompatActivity() {

    private var countdownTimer: CountDownTimer? = null
    private var overtimeTimer: CountDownTimer? = null
    private var ringtone: Ringtone? = null
    private lateinit var vibrator: Vibrator

    private var timeLeftInMillis: Long = 0
    private var totalTimeInMillis: Long = 0
    private var overtimeSeconds: Int = 0

    private var isTimerRunning = false
    private var isTimerFinished = false

    // Views
    private lateinit var llSetupView: LinearLayout
    private lateinit var rlRunningView: RelativeLayout
    private lateinit var tvTimerDisplay: TextView
    private lateinit var tvWaktuHabis: TextView
    private lateinit var pbTimer: ProgressBar
    private lateinit var pickerHours: NumberPicker
    private lateinit var pickerMinutes: NumberPicker
    private lateinit var pickerSeconds: NumberPicker
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnReset: Button
    private lateinit var btnPreset10: Button
    private lateinit var btnPreset15: Button
    private lateinit var btnPreset30: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        val toolbar: Toolbar = findViewById(R.id.timer_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Timer Memasak"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        bindViews()
        setupPickers()
        setupButtons()
        switchViews(false)
    }

    private fun bindViews() {
        llSetupView = findViewById(R.id.ll_setup_view)
        rlRunningView = findViewById(R.id.rl_running_view)
        tvTimerDisplay = findViewById(R.id.tv_timer_display)
        tvWaktuHabis = findViewById(R.id.tv_waktu_habis)
        pbTimer = findViewById(R.id.pb_timer)
        pickerHours = findViewById(R.id.picker_hours)
        pickerMinutes = findViewById(R.id.picker_minutes)
        pickerSeconds = findViewById(R.id.picker_seconds)
        btnStart = findViewById(R.id.btn_start)
        btnPause = findViewById(R.id.btn_pause)
        btnReset = findViewById(R.id.btn_reset)
        btnPreset10 = findViewById(R.id.btn_preset_10)
        btnPreset15 = findViewById(R.id.btn_preset_15)
        btnPreset30 = findViewById(R.id.btn_preset_30)
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
        btnStart.setOnClickListener { startTimerFromPickers() }

        btnPause.setOnClickListener {
            when {
                isTimerFinished -> restartTimer() // Mulai Ulang
                isTimerRunning -> pauseTimer()
                else -> resumeTimer()
            }
        }

        btnReset.setOnClickListener {
            if(isTimerFinished) {
                resetTimer() // Abaikan
            } else {
                resetTimer() // Hapus
            }
        }

        btnPreset10.setOnClickListener { startPresetTimer(10) }
        btnPreset15.setOnClickListener { startPresetTimer(15) }
        btnPreset30.setOnClickListener { startPresetTimer(30) }
    }

    private fun startPresetTimer(minutes: Int) {
        totalTimeInMillis = minutes * 60 * 1000L
        startCountdown()
    }

    private fun startTimerFromPickers() {
        val hours = pickerHours.value
        val minutes = pickerMinutes.value
        val seconds = pickerSeconds.value
        totalTimeInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L
        startCountdown()
    }

    private fun startCountdown() {
        if (totalTimeInMillis == 0L) return

        timeLeftInMillis = totalTimeInMillis
        switchViews(true)

        countdownTimer = object : CountDownTimer(timeLeftInMillis, 50) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay(false)
            }

            override fun onFinish() {
                handleTimerFinished()
            }
        }.start()

        isTimerRunning = true
        btnPause.text = "Jeda"
    }

    private fun handleTimerFinished() {
        isTimerRunning = false
        isTimerFinished = true
        pbTimer.progress = 0
        tvWaktuHabis.visibility = View.VISIBLE
        btnPause.text = "Mulai Ulang"
        btnReset.text = "Abaikan"
        playAlarm()
        startOvertimeCounter()
    }

    private fun startOvertimeCounter() {
        overtimeSeconds = 0
        overtimeTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                overtimeSeconds++
                updateTimerDisplay(true)
            }
            override fun onFinish() {}
        }.start()
    }

    private fun pauseTimer() {
        countdownTimer?.cancel()
        isTimerRunning = false
        btnPause.text = "Lanjut"
    }

    private fun resumeTimer() {
        // Recreate countdown with remaining time
        countdownTimer = object : CountDownTimer(timeLeftInMillis, 50) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay(false)
            }
            override fun onFinish() {
                handleTimerFinished()
            }
        }.start()

        isTimerRunning = true
        btnPause.text = "Jeda"
    }

    private fun restartTimer() {
        stopAlarm()
        overtimeTimer?.cancel()
        isTimerFinished = false
        startCountdown() // Restart with the same totalTimeInMillis
    }

    private fun resetTimer() {
        countdownTimer?.cancel()
        overtimeTimer?.cancel()
        stopAlarm()

        timeLeftInMillis = 0
        totalTimeInMillis = 0
        isTimerRunning = false
        isTimerFinished = false
        
        switchViews(false)
    }

    private fun switchViews(isRunning: Boolean) {
        if (isRunning) {
            llSetupView.visibility = View.GONE
            rlRunningView.visibility = View.VISIBLE
            tvWaktuHabis.visibility = View.GONE
            btnReset.text = "Hapus"
        } else {
            llSetupView.visibility = View.VISIBLE
            rlRunningView.visibility = View.GONE
        }
    }

    private fun updateTimerDisplay(isOvertime: Boolean) {
        val timeToShow = if(isOvertime) overtimeSeconds.toLong() * 1000 else timeLeftInMillis
        val hours = (timeToShow / 1000) / 3600
        val minutes = ((timeToShow / 1000) % 3600) / 60
        val seconds = (timeToShow / 1000) % 60
        val sign = if (isOvertime) "-" else ""

        tvTimerDisplay.text = String.format(Locale.getDefault(), "%s%02d:%02d:%02d", sign, hours, minutes, seconds)

        if (!isOvertime && totalTimeInMillis > 0) {
            pbTimer.progress = (timeLeftInMillis * 100 / totalTimeInMillis).toInt()
        }
    }

    private fun playAlarm() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)
            ringtone?.play()

            val pattern = longArrayOf(0, 1000, 1000) // Vibrate, wait, vibrate
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0)) // a repeat index of 0 will repeat the pattern indefinitely
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, 0)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAlarm() {
        ringtone?.stop()
        vibrator.cancel()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (isTimerRunning || isTimerFinished) {
            resetTimer()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up to prevent leaks
        countdownTimer?.cancel()
        overtimeTimer?.cancel()
        stopAlarm()
    }
}
