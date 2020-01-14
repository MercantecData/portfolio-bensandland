package com.example.locationandthings

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Switch
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val backGround = findViewById<View>(R.id.mainView)
        val toggleBtn = findViewById<Switch>(R.id.toggleTheme)

        toggleBtn.setOnCheckedChangeListener{view, isChecked ->
            if (isChecked)
            {
                backGround.setBackgroundColor(Color.DKGRAY)
                toggleBtn.setTextColor(Color.WHITE)
            }
            else
            {
                toggleBtn.setTextColor(Color.BLACK)
                backGround.setBackgroundColor(Color.WHITE)
            }
        }
    }
}
