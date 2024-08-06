package com.laraib.smd_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class places : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var placesList: MutableList<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places)
        databaseRef = FirebaseDatabase.getInstance().getReference("places")
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.placesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        placesList = mutableListOf()

        // Initialize and set adapter
        placeAdapter = PlaceAdapter(this, placesList)
        recyclerView.adapter = placeAdapter

        // Retrieve data from Firebase
        fetchDataFromFirebase()
        val homeButton = findViewById<ImageView>(R.id.homebutton)
        homeButton.setOnClickListener {
            val intent = Intent(this@places, home::class.java)
            startActivity(intent)
        }
        val backButton = findViewById<ImageView>(R.id.backbutton)
        backButton.setOnClickListener {
            val intent = Intent(this@places, home::class.java)
            startActivity(intent)
        }
    }

    private fun fetchDataFromFirebase() {
        // Add a listener to fetch data from Firebase
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                placesList.clear() // Clear the list before adding new data
                for (placeSnapshot in snapshot.children) {
                    val place = placeSnapshot.getValue(Place::class.java)
                    place?.let {
                        place.id = placeSnapshot.key ?: "" // Assign the Firebase key to id field
                        placesList.add(place)
                    }
                }
                placeAdapter.notifyDataSetChanged() // Notify the adapter of changes
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("placesActivity", "Error fetching data from Firebase: ${error.message}")
            }
        })
    }

}
