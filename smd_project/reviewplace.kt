package com.laraib.smd_project

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.*

class reviewplace : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviewplace)

        // Retrieve placeId from intent
        val placeId = intent.getStringExtra("placeId")
        placeId?.let {
            Log.d("reviewplace", "Fetched placeId: $placeId") // Log the fetched placeId

            // Initialize Firebase database reference for the specific place
            databaseRef = FirebaseDatabase.getInstance().getReference("places").child(placeId)

            // Update UI with place data
            fetchPlaceData()
        } ?: run {
            Log.e("reviewplace", "No placeId found in intent")
            // Handle the case where placeId is null, maybe show an error message
        }
        val backButton = findViewById<ImageView>(R.id.backbutton)
        backButton.setOnClickListener {
            // Navigate back to places activity and pass placeId as an extra string
            val intent = Intent(this@reviewplace, placepage::class.java)
            intent.putExtra("placeId", placeId)
            startActivity(intent)
        }
        val homeButton = findViewById<ImageView>(R.id.homebutton)
        homeButton.setOnClickListener {
            val intent = Intent(this@reviewplace, home::class.java)
            startActivity(intent)
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
                    Log.e("reviewplace", "No data found for placeId: ${databaseRef.key}")
                    // Handle the case where place data is null
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("reviewplace", "Error fetching place data: ${error.message}")
            }
        })
    }

    private fun updateUI(place: Place) {
        // Update ImageView with place image
        Glide.with(this)
            .load(place.imageURL) // Load the image from URL
            .placeholder(R.drawable.images_1) // Placeholder image
            .error(R.drawable.image) // Error image
            .into(findViewById<ImageView>(R.id.placeimg)) // ImageView to load the image into

        // Update TextView with place name
        findViewById<TextView>(R.id.placename).text = place.name

        // Save the review when the save button is clicked
        findViewById<TextView>(R.id.savebutton).setOnClickListener {
            saveReviewAndRating(place.id, place.name)
        }
    }

    private fun saveReviewAndRating(placeId: String, placeName: String) {
        // Get the review text and rating from EditText and RatingBar
        val reviewText = findViewById<EditText>(R.id.reviewtext)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)

        val review = Review(
            reviewText.text.toString(),
            ratingBar.rating,
            placeName
        )

        val reviewKey = databaseRef.child("reviews").push().key ?: ""
        databaseRef.child("reviews").child(reviewKey).setValue(review)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Review added successfully", Toast.LENGTH_SHORT).show()
                // Clear the review text and reset the rating after successful addition
                reviewText.setText("")
                ratingBar.rating = 0f
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Failed to add review", Toast.LENGTH_SHORT).show()
            }

        // Associate the review with the place
        databaseRef.child("reviews").child(reviewKey).child("placeId").setValue(placeId)
    }


}
