//package com.laraib.smd_project
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import androidx.recyclerview.widget.RecyclerView
//
//class showreviewsguide : AppCompatActivity() {
//    private lateinit var toprv: RecyclerView
//    var adapter: review_adapter? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_showreviewsguide)
//    }
//}

package com.laraib.smd_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class showreviewsguide : AppCompatActivity() {

    private lateinit var toprv: RecyclerView
    private var adapter: review_adapter? = null
    private val reviewList = mutableListOf<reviewclass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showreviewsguide)

        // Initialize RecyclerView
        toprv = findViewById(R.id.top_mentors_rv)
        toprv.setHasFixedSize(true)
        toprv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Initialize Adapter
        adapter = review_adapter(reviewList)
        toprv.adapter = adapter

        // Retrieve reviews from Firebase
        retrieveReviewsFromFirebase()
    }

    private fun retrieveReviewsFromFirebase() {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("guides")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                reviewList.clear()
                for (snapshot in dataSnapshot.children) {
                    val userId = snapshot.child("name").getValue(String::class.java)
                    val reviewText = snapshot.child("place").getValue(String::class.java)
                    val image= snapshot.child("image").getValue(String::class.java)

                    val review = reviewclass(image, userId, reviewText)
                    reviewList.add(review)
                }
                adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}



//private fun retrieveReviews() {
//    // Add a listener to fetch data from Firebase
//    databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            // Clear the existing reviews list
//            reviewsList.clear()
//
//            // Iterate through each place node
//            for (placeSnapshot in dataSnapshot.children) {
//                // Check if the place has reviews
//                if (placeSnapshot.hasChild("reviews")) {
//                    // Retrieve reviews for this place
//                    val reviewsSnapshot = placeSnapshot.child("reviews")
//                    for (reviewSnapshot in reviewsSnapshot.children) {
//                        val reviewText = reviewSnapshot.child("reviewText").getValue(String::class.java) ?: ""
//                        val rating = reviewSnapshot.child("rating").getValue(Float::class.java) ?: 0f
//                        val placeName = placeSnapshot.child("name").getValue(String::class.java) ?: ""
//
//                        // Create a Review object with retrieved data
//                        val review = Review(reviewText, rating, placeName)
//
//                        // Add the review to the list
//                        reviewsList.add(review)
//                    }
//                }
//            }
//
//            // Initialize adapter
//            reviewsAdapter = ReviewsAdapter(reviewsList)
//
//            // Set the adapter to RecyclerView
//            recyclerView.adapter = reviewsAdapter
//
//            // Log number of reviews found
//            Log.d("seereviews", "Number of reviews found: ${reviewsList.size}")
//        }
//
//        override fun onCancelled(databaseError: DatabaseError) {
//            // Handle database error
//            Log.e("seereviews", "Error fetching reviews: ${databaseError.message}")
//        }
//    })
//}
