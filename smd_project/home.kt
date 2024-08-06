package com.laraib.smd_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*

class home : AppCompatActivity() {

    private lateinit var topRecyclerView: RecyclerView
    private lateinit var mentorAdapter: MentorAdapter
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        topRecyclerView = findViewById(R.id.topRecyclerView)
        topRecyclerView.setHasFixedSize(true)
        topRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        databaseRef = FirebaseDatabase.getInstance().getReference("places")

        retrieveTopReviewedPlaces()

        val reviewbutton = findViewById<ImageView>(R.id.reviewbutton)
        reviewbutton.setOnClickListener {
            val intent = Intent(this@home, seereviews::class.java)
            startActivity(intent)
        }

        val placeButton = findViewById<ImageView>(R.id.placebutton)
        placeButton.setOnClickListener {
            val intent = Intent(this@home, places::class.java)
            startActivity(intent)
        }

        val chatButton = findViewById<ImageView>(R.id.chatbutton)
        chatButton.setOnClickListener {
            val intent = Intent(this@home, chat_traveller::class.java)
            startActivity(intent)
        }

        val guides = findViewById<ImageView>(R.id.guides)
        guides.setOnClickListener {
            val intent = Intent(this@home, guides::class.java)
            startActivity(intent)
        }

        val eventButton = findViewById<ImageView>(R.id.event)
        eventButton.setOnClickListener {
            val intent = Intent(this@home, eventpage::class.java)
            startActivity(intent)
        }

        val homeButton = findViewById<ImageView>(R.id.homebutton)
        homeButton.setOnClickListener {
            val intent = Intent(this@home, home::class.java)
            startActivity(intent)
        }
    }

    private fun retrieveTopReviewedPlaces() {
        databaseRef.orderByChild("reviews").limitToLast(3)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val places = mutableListOf<Mentor>()

                    for (placeSnapshot in dataSnapshot.children) {
                        val name = placeSnapshot.child("name").getValue(String::class.java) ?: ""
                        val location = placeSnapshot.child("location").getValue(String::class.java) ?: ""
                        val imageURL = placeSnapshot.child("imageURL").getValue(String::class.java) ?: ""

                        val place = Mentor(name, location, imageURL)
                        places.add(place)
                    }

                    mentorAdapter = MentorAdapter(places, this@home)
                    topRecyclerView.adapter = mentorAdapter
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                }
            })
    }
}
