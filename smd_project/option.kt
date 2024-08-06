package com.laraib.smd_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class option : AppCompatActivity() {

    private lateinit var guide: Button
    private lateinit var tourist: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)

        guide = findViewById(R.id.button1)
        tourist = findViewById(R.id.button2)

        guide.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, signup_guide::class.java)
            startActivity(intent)
        }
        tourist.setOnClickListener() {
            // Navigate to a new page here
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
        }

    }
}