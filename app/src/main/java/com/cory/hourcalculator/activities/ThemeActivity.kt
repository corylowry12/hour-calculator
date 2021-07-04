package com.cory.hourcalculator.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cory.hourcalculator.R
import com.cory.hourcalculator.classes.*
import com.google.android.gms.ads.*

class ThemeActivity : AppCompatActivity() {

    private lateinit var darkThemeData: DarkThemeData
    private lateinit var accentColor: AccentColor
    private lateinit var vibrationData: VibrationData

    val testDeviceId = listOf("5E80E48DC2282D372EAE0E3ACDE070CC", "8EE44B7B4B422D333731760574A381FE")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        darkThemeData = DarkThemeData(this)
        if (darkThemeData.loadDarkModeState()) {
            setTheme(R.style.AMOLED)
        } else {
            setTheme(R.style.AppTheme)
        }
        accentColor = AccentColor(this)
        when {
            accentColor.loadAccent() == 0 -> {
                theme.applyStyle(R.style.teal_accent, true)
            }
            accentColor.loadAccent() == 1 -> {
                theme.applyStyle(R.style.pink_accent, true)
            }
            accentColor.loadAccent() == 2 -> {
                theme.applyStyle(R.style.orange_accent, true)
            }
            accentColor.loadAccent() == 3 -> {
                theme.applyStyle(R.style.red_accent, true)
            }
        }
        setContentView(R.layout.activity_theme)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        MobileAds.initialize(this)
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-4546055219731501/5171269817"
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceId).build()
        MobileAds.setRequestConfiguration(configuration)
        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {
        }

        vibrationData = VibrationData(this)

        val lightThemeButton = findViewById<RadioButton>(R.id.lightTheme)
        val darkThemeButton = findViewById<RadioButton>(R.id.darkTheme)

        if (darkThemeData.loadDarkModeState()) {
            darkThemeButton.isChecked = true
        } else if (!darkThemeData.loadDarkModeState()) {
            lightThemeButton.isChecked = true
        }

        lightThemeButton.setOnClickListener {
            vibration(vibrationData)
            if(!darkThemeData.loadDarkModeState()) {
                Toast.makeText(this, getString(R.string.light_theme_is_already_enabled), Toast.LENGTH_SHORT).show()
            }
            else {
                darkThemeData.setDarkModeState(false)
                restartApplication()
            }
        }
        darkThemeButton.setOnClickListener {
            vibration(vibrationData)
            if (darkThemeData.loadDarkModeState()) {
                Toast.makeText(this, getString(R.string.dark_mode_is_already_enabled), Toast.LENGTH_SHORT).show()
            } else {
                darkThemeData.setDarkModeState(true)
                restartApplication()
            }
        }

        val tealAccentButton = findViewById<RadioButton>(R.id.Teal)
        val pinkAccentButton = findViewById<RadioButton>(R.id.Pink)
        val orangeAccentButton = findViewById<RadioButton>(R.id.Orange)
        val redAccentButton = findViewById<RadioButton>(R.id.Red)

        if (accentColor.loadAccent() == 0) {
            tealAccentButton.isChecked = true
        }
        else if (accentColor.loadAccent() == 1) {
            pinkAccentButton.isChecked = true
        }
        else if (accentColor.loadAccent() == 2) {
            orangeAccentButton.isChecked = true
        }
        else if (accentColor.loadAccent() == 3) {
            redAccentButton.isChecked = true
        }

        tealAccentButton.setOnClickListener {
            vibration(vibrationData)
            accentColor.setAccentState(0)
            restartApplication()
        }
        pinkAccentButton.setOnClickListener {
            vibration(vibrationData)
            accentColor.setAccentState(1)
            restartApplication()
        }
        orangeAccentButton.setOnClickListener {
            vibration(vibrationData)
            accentColor.setAccentState(2)
            restartApplication()
        }
        redAccentButton.setOnClickListener {
            vibration(vibrationData)
            accentColor.setAccentState(3)
            restartApplication()
        }
    }

    private fun vibration(vibrationData: VibrationData) {
        if (vibrationData.loadVibrationState()) {
            val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun restartApplication() {
        val intent = this.intent
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        if (!PerformanceModeData(this).loadPerformanceMode()) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        } else {
            overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
        }
    }

    override fun onRestart() {
        super.onRestart()
        val intent = Intent(this, this::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        if (!PerformanceModeData(this).loadPerformanceMode()) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        } else {
            overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
        if (!PerformanceModeData(this).loadPerformanceMode()) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        } else {
            overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu_appearance_settings, menu)
        val historyToggleData = HistoryToggleData(this)
        if (!historyToggleData.loadHistoryState()) {
            val history = menu.findItem(R.id.history)
            history.isVisible = false
            val trash = menu.findItem(R.id.trash)
            trash.isVisible = false
            val graph = menu.findItem(R.id.graph)
            graph.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        vibrationData = VibrationData(this)
        if (vibrationData.loadVibrationState()) {
            val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        return when (item.itemId) {
            R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                if(!PerformanceModeData(this).loadPerformanceMode()) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
                else {
                    overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
                }
                return true
            }
            R.id.Settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                if(!PerformanceModeData(this).loadPerformanceMode()) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
                else {
                    overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
                }
                return true
            }
            R.id.changelog -> {
                val intent = Intent(this, PatchNotesActivity::class.java)
                startActivity(intent)
                if(!PerformanceModeData(this).loadPerformanceMode()) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
                else {
                    overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
                }
                return true
            }
            R.id.history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                if(!PerformanceModeData(this).loadPerformanceMode()) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
                else {
                    overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
                }
                return true
            }
            R.id.trash -> {
                val intent = Intent(this, TrashActivity::class.java)
                startActivity(intent)
                if(!PerformanceModeData(this).loadPerformanceMode()) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
                else {
                    overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
                }
                return true
            }
            R.id.graph -> {
                val intent = Intent(this, GraphActivity::class.java)
                startActivity(intent)
                if(!PerformanceModeData(this).loadPerformanceMode()) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
                else {
                    overridePendingTransition(R.anim.no_animation, R.anim.no_animation)
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}