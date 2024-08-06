package com.laraib.smd_project

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class addplace : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var eventsEditText: EditText
    private lateinit var saveButton: TextView
    private lateinit var uploadPictureButton: ImageView

    private var imageURL: String = ""
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addplace)

        val chatButton = findViewById<ImageView>(R.id.chatbutton)
        chatButton.setOnClickListener {
            val intent = Intent(this, chat_guide::class.java)
            startActivity(intent)
        }


        val homeButton = findViewById<ImageView>(R.id.homebutton)
        homeButton.setOnClickListener {
            val intent = Intent(this, home::class.java)
            startActivity(intent)
        }

        nameEditText = findViewById(R.id.name)
        locationEditText = findViewById(R.id.location)
        descriptionEditText = findViewById(R.id.description)
        eventsEditText = findViewById(R.id.events)
        saveButton = findViewById(R.id.savebutton)
        uploadPictureButton = findViewById(R.id.uploadpicture)

        uploadPictureButton.setOnClickListener {
            openGalleryForImage()
        }

        saveButton.setOnClickListener {
            savePlaceData()
            NotificationHelper(this,"Place Added Sucessfully").Notification()
            val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                this.uri = uri
                // You can display the selected image here if needed
            }
        }

    private fun openGalleryForImage() {
        // Launch the gallery to select an image
        galleryLauncher.launch("image/*")
    }

    private fun savePlaceData() {
        val name = nameEditText.text.toString().trim()
        val location = locationEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val events = eventsEditText.text.toString().trim()

        if (name.isEmpty() || location.isEmpty() || description.isEmpty() || events.isEmpty() || uri == null) {
            // Show a toast message prompting to fill relevant fields
            Toast.makeText(this, "Please fill all relevant fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique key for the place
        val placeId = UUID.randomUUID().toString()

        // Upload image to Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference.child("images/places/$placeId")
        uri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Get the download URL of the image
                        imageURL = downloadUri.toString()

                        // Save place data to Firebase Realtime Database
                        val databaseRef = FirebaseDatabase.getInstance().reference.child("places").child(placeId)
                        val placeData = mapOf(
                            "name" to name,
                            "location" to location,
                            "description" to description,
                            "eventDes" to events,
                            "imageURL" to imageURL
                        )
                        databaseRef.setValue(placeData)
                            .addOnSuccessListener {
                                // Data successfully saved
                                // You can show a success message here
                                Toast.makeText(this, "Place added successfully", Toast.LENGTH_SHORT).show()
                                // Clear the fields after successful addition
                                clearFields()
                            }
                            .addOnFailureListener { exception ->
                                // Handle errors
                                Toast.makeText(this, "Failed to add place: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun clearFields() {
        nameEditText.text.clear()
        locationEditText.text.clear()
        descriptionEditText.text.clear()
        eventsEditText.text.clear()
        uri = null
    }
}
