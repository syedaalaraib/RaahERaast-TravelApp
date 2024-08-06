package com.laraib.smd_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class eventpage : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var eventsList: MutableList<Event>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventpage)

        recyclerView = findViewById(R.id.eventsrecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        eventsList = mutableListOf()
        eventsAdapter = EventsAdapter(this, eventsList)
        recyclerView.adapter = eventsAdapter

        // Initialize Firebase database reference for the events node
        databaseRef = FirebaseDatabase.getInstance().getReference("events")

        // Retrieve data from Firebase
        fetchDataFromFirebase()
        val home = findViewById<ImageView>(R.id.homebutton)
        home.setOnClickListener {
            val intent = Intent(this@eventpage, home::class.java)
            startActivity(intent)
        }
        val back = findViewById<ImageView>(R.id.backbutton)
        back.setOnClickListener {
            val intent = Intent(this@eventpage, home::class.java)
            startActivity(intent)
        }
    }

    private fun fetchDataFromFirebase() {
        // Add a listener to fetch data from Firebase
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventsList.clear() // Clear the list before adding new data
                for (eventSnapshot in snapshot.children) {
                    val imageURL = eventSnapshot.child("imageURL").getValue(String::class.java) ?: ""
                    val name = eventSnapshot.child("name").getValue(String::class.java) ?: ""
                    val date = eventSnapshot.child("date").getValue(String::class.java) ?: ""
                    val location = eventSnapshot.child("location").getValue(String::class.java) ?: ""
                    eventsList.add(Event(imageURL, name, date, location))
                }
                eventsAdapter.notifyDataSetChanged() // Notify the adapter of changes
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
