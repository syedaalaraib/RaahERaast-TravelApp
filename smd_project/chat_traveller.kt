package com.laraib.smd_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import user_adapter

class chat_traveller : AppCompatActivity() {

    private lateinit var toprv: RecyclerView
    private lateinit var adapter: user_adapter
    private val guideList = mutableListOf<User_rv>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_traveller)


        // Initialize RecyclerView
        toprv = findViewById(R.id.top_mentors_rv)
        toprv.setHasFixedSize(true)
        toprv.layoutManager = LinearLayoutManager(this)

        // Create adapter
        adapter = user_adapter(guideList, this)
        toprv.adapter = adapter

        // Retrieve and display registered guides
        retrieveRegisteredGuides()
    }

    private fun retrieveRegisteredGuides() {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("guides")

//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                guideList.clear()
//                for (snapshot in dataSnapshot.children) {
//                    val guide = snapshot.getValue(User_rv::class.java)
//                    guide?.let {
//                        guideList.add(it)
//                    }
//                }
//                adapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle error
//            }
//        })
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                guideList.clear()
                for (snapshot in dataSnapshot.children) {
                    val photolink = snapshot.child("image").getValue(String::class.java)
                    val name = snapshot.child("name").getValue(String::class.java)
                    val uid = snapshot.key // Assuming the user ID is the key of the snapshot

                    // Check if photolink is not null or empty before adding to the list
                    if (!photolink.isNullOrEmpty()) {
                        val user = User_rv()
                        user.photolink = photolink
                        user.name = name
                        user.uid = uid
                        guideList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

    }
}
