package com.example.locationandstuff

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Switch
import android.content.SharedPreferences
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R.id.toggle


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Set theme on start up by getting id and then calling setTheme with that as parameter
        val bundle :Bundle ?=intent.extras
        val theme_id = bundle?.getInt("theme")

        when (theme_id)
        {
            1 -> setTheme(R.style.darkTheme)
            else -> {
                setTheme(R.style.lightTheme)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Get dark/light theme switch button
        val toggleBtn = findViewById<Switch>(R.id.toggleTheme)
        // Get state of switch button
        val sharedPrefs =
            getSharedPreferences("com.example.locationandstuff", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        // Get state
        toggleBtn.isChecked = sharedPrefs.getBoolean("toggleTheme", false)

        // Create event listener for toggle button
        toggleBtn.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                // Set state
                editor.putBoolean("toggleTheme", true)
                editor.commit()

                // Set to dark mode
                changeTheme(1)

            } else {
                // Set state
                editor.putBoolean("toggleTheme", false)
                editor.commit()

                // Set to light mode
                changeTheme()
            }
        }

    }

    fun changeTheme(i: Int = -1) {
        val intent = intent
        intent.putExtra("theme", i)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(intent)
    }
}
