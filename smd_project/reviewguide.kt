package com.laraib.smd_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*

class reviewguide : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var imageImageView: ImageView
    private lateinit var review: TextView
    private lateinit var reviewButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviewguide)

        // Initialize UI elements
        nameTextView = findViewById(R.id.name)
        imageImageView = findViewById(R.id.image)
        review = findViewById(R.id.review)
        reviewButton = findViewById(R.id.button)

        // Receive the guide ID sent from the previous page
        val guideId = intent.getStringExtra("mentorId") ?: ""

        // Retrieve guide's information from Firebase Realtime Database
        retrieveGuideInfoFromFirebase(guideId)
        // Inside your activity or fragment where you want to save the review


        reviewButton.setOnClickListener {
            saveReviewToFirebase(guideId, review.text.toString()) // Pass the guide ID and the review text
            val intent = Intent(this, showreviewsguide::class.java)
            startActivity(intent)
        }


    }

    private fun retrieveGuideInfoFromFirebase(guideId: String) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("guides").child(guideId)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val guide = dataSnapshot.getValue(Item_RV::class.java)
                val photolink = dataSnapshot.child("image").getValue(String::class.java)
                val name = dataSnapshot.child("name").getValue(String::class.java)

                guide?.let {
                    // Populate UI elements with guide's information
                    nameTextView.text = name
                    // Load image using Glide
                    Glide.with(this@reviewguide).load(photolink).into(imageImageView)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun saveReviewToFirebase(guideId: String, reviewText: String) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("guides").child(guideId).child("reviews")

        // Generate a unique key for the review
        val reviewId = databaseReference.push().key

        reviewId?.let { id ->
            // Add the review to the "reviews" node under the guide with the generated ID
            databaseReference.child(id).setValue(reviewText)
        }
    }
}
