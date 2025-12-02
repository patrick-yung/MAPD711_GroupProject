package com.mapd711_groupproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val enterBtn = findViewById<Button>(R.id.btnEnter)
        enterBtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}
