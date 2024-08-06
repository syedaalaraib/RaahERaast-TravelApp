package com.laraib.smd_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class guidehome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guidehome)

        val reviewbutton = findViewById<ImageView>(R.id.reviewbutton)
        reviewbutton.setOnClickListener {
            val intent = Intent(this, seereviews::class.java)
            startActivity(intent)
        }

        val placeButton = findViewById<ImageView>(R.id.placebutton)
        placeButton.setOnClickListener {
            val intent = Intent(this, addplace::class.java)
            startActivity(intent)
        }

        val chatButton = findViewById<ImageView>(R.id.chatbutton)
        chatButton.setOnClickListener {
            val intent = Intent(this, chat_guide::class.java)
            startActivity(intent)
        }

        val guides = findViewById<ImageView>(R.id.contacts)
        guides.setOnClickListener {
            val intent = Intent(this, guides::class.java)
            startActivity(intent)
        }

        val eventButton = findViewById<ImageView>(R.id.event)
        eventButton.setOnClickListener {
            val intent = Intent(this, addevent::class.java)
            startActivity(intent)
        }

        val homeButton = findViewById<ImageView>(R.id.homebutton)
        homeButton.setOnClickListener {
            val intent = Intent(this, home::class.java)
            startActivity(intent)
        }
    }
}