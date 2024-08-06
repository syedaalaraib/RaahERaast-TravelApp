package com.laraib.smd_project

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class seereviews : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewsAdapter: ReviewsAdapter
    private lateinit var reviewsList: MutableList<Review>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seereviews)

        recyclerView = findViewById(R.id.reviewsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance().reference.child("places")

        // Initialize reviews list
        reviewsList = mutableListOf()

        // Retrieve reviews from Firebase
        retrieveReviews()
        val home = findViewById<ImageView>(R.id.homebutton)
        home.setOnClickListener {
            val intent = Intent(this@seereviews, home::class.java)
            startActivity(intent)
        }
        val back = findViewById<ImageView>(R.id.backbutton)
        back.setOnClickListener {
            val intent = Intent(this@seereviews, home::class.java)
            startActivity(intent)
        }
    }

    private fun retrieveReviews() {
        // Add a listener to fetch data from Firebase
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the existing reviews list
                reviewsList.clear()

                // Iterate through each place node
                for (placeSnapshot in dataSnapshot.children) {
                    // Check if the place has reviews
                    if (placeSnapshot.hasChild("reviews")) {
                        // Retrieve reviews for this place
                        val reviewsSnapshot = placeSnapshot.child("reviews")
                        for (reviewSnapshot in reviewsSnapshot.children) {
                            val reviewText = reviewSnapshot.child("reviewText").getValue(String::class.java) ?: ""
                            val rating = reviewSnapshot.child("rating").getValue(Float::class.java) ?: 0f
                            val placeName = placeSnapshot.child("name").getValue(String::class.java) ?: ""

                            // Create a Review object with retrieved data
                            val review = Review(reviewText, rating, placeName)

                            // Add the review to the list
                            reviewsList.add(review)
                        }
                    }
                }

                // Initialize adapter
                reviewsAdapter = ReviewsAdapter(reviewsList)

                // Set the adapter to RecyclerView
                recyclerView.adapter = reviewsAdapter

                // Log number of reviews found
                Log.d("seereviews", "Number of reviews found: ${reviewsList.size}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Log.e("seereviews", "Error fetching reviews: ${databaseError.message}")
            }
        })
    }
}
