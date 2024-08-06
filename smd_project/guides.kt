package com.laraib.smd_project
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class guides : AppCompatActivity() {

    private lateinit var toprv: RecyclerView
    val mentorsList = mutableListOf<Item_RV>()
    var adapter: guide_adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guides)

        // Initialize RecyclerViews
        toprv = findViewById(R.id.top_mentors_rv)
        toprv.setHasFixedSize(true)
        toprv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Initialize Adapter
        adapter = guide_adapter(mentorsList) { mentorId ->
            // Handle item click here
            // For example, navigate to the next page with the mentor ID
            val intent = Intent(this, reviewguide::class.java)
            Toast.makeText(this, "mentorid $mentorId", Toast.LENGTH_SHORT).show()
            intent.putExtra("mentorId", mentorId)
            startActivity(intent)
        }


        // Create and set adapter
        retrieveMentorsFromRealtimeDatabase()
        toprv.adapter = adapter


    }

    private fun retrieveMentorsFromRealtimeDatabase() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("guides")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mentorsList.clear()
                for (snapshot in dataSnapshot.children) {
                    val photolink = snapshot.child("image").getValue(String::class.java)
                    val name = snapshot.child("name").getValue(String::class.java)
                    val place = snapshot.child("place").getValue(String::class.java)
                    val uid = snapshot.key // Assuming the user ID is the key of the snapshot

                    // Check if photolink is not null or empty before adding to the list
                    if (!photolink.isNullOrEmpty()) {
                        val user = Item_RV()
                        user.image1 = photolink
                        user.name = name
                        user.uid = uid
                        user.place = place
                        mentorsList.add(user)
                    }
                }
                adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

}









//package com.laraib.smd_project
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//
//class guides : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_guides)
//    }
//}