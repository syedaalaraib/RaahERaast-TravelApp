package com.laraib.smd_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*

class placepage : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private var placeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placepage)

        // Retrieve placeId from intent
        placeId = intent.getStringExtra("placeId")
        placeId?.let {
            Log.d("placepage", "Fetched placeId: $placeId") // Log the fetched placeId
            val reviewButton = findViewById<TextView>(R.id.reviewbutton)

            // Set a click listener to move to review page and pass placeId
            reviewButton.setOnClickListener {
                val intent = Intent(this, reviewplace::class.java)
                intent.putExtra("placeId", placeId)
                startActivity(intent)
            }
            val homeButton = findViewById<ImageView>(R.id.homebutton)
            homeButton.setOnClickListener {
                val intent = Intent(this@placepage, home::class.java)
                startActivity(intent)
            }
            val backButton = findViewById<ImageView>(R.id.backbutton)
            backButton.setOnClickListener {
                val intent = Intent(this@placepage, places::class.java)
                startActivity(intent)
            }
            // Initialize Firebase database reference for the specific place
            databaseRef = FirebaseDatabase.getInstance().getReference("places").child(placeId!!)

            // Update UI with place data
            fetchPlaceData()
        } ?: run {
            Log.e("placepage", "No placeId found in intent")
            // Handle the case where placeId is null, maybe show an error message
        }
    }

    private fun fetchPlaceData() {
        // Add a listener to fetch data from Firebase
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val place = snapshot.getValue(Place::class.java)
                place?.let {
                    updateUI(place)
                } ?: run {
                    Log.e("placepage", "No data found for placeId: ${databaseRef.key}")
                    // Handle the case where place data is null
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("placepage", "Error fetching place data: ${error.message}")
            }
        })
    }

    private fun updateUI(place: Place) {
        Log.d("placepage", "Event: ${place.eventDes}")
        // Update TextViews with place information
        findViewById<TextView>(R.id.name).text = place.name
        findViewById<TextView>(R.id.location).text = place.location
        findViewById<TextView>(R.id.descriptiontext).text = place.description
        findViewById<TextView>(R.id.eventoccuring).text = place.eventDes

        // Load image using Glide
        Glide.with(this)
            .load(place.imageURL) // Load the image from URL
            .placeholder(R.drawable._41ed5717faddc001d7a0aa3) // Placeholder image
            .error(R.drawable.image) // Error image
            .into(findViewById<ImageView>(R.id.placeimage)) // ImageView to load the image into

        // Calculate average rating and update TextView
        placeId?.let { calculateAverageRating(it) }
    }

    private fun calculateAverageRating(placeId: String) {
        // Get reference to the reviews for this place
        val reviewsRef = databaseRef.child("reviews")

        // Add a listener to fetch reviews from Firebase
        reviewsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalRating = 0f
                var reviewCount = 0

                // Iterate through each review and calculate total rating
                for (reviewSnapshot in snapshot.children) {
                    val review = reviewSnapshot.getValue(Review::class.java)
                    review?.let {
                        totalRating += review.rating
                        reviewCount++
                    }
                }

                // Calculate average rating
                val averageRating = if (reviewCount > 0) totalRating / reviewCount else 0f

                // Update UI with average rating
                findViewById<TextView>(R.id.ratingvalue).text = String.format("%.1f", averageRating)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("placepage", "Error fetching reviews: ${error.message}")
            }
        })
    }
}
