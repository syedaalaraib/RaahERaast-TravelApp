package com.laraib.smd_project
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter

import java.io.ByteArrayOutputStream
import java.io.InputStream
import com.laraib.smd_project.travellerdata
import java.util.UUID

class signup : AppCompatActivity() {

    private lateinit var citySpinner: Spinner
    private lateinit var nametextView: TextView
    private lateinit var emailEditText: TextView
    private lateinit var passwordEditText: TextView
    private lateinit var signup: TextView
    private lateinit var image: ImageView


    var fileuri: Uri? = null
    private lateinit var bitmap: Bitmap
    private var encodedImage: String = ""

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()


        //go to the login page
        val Button112 = findViewById<TextView>(R.id.login)
        // Set OnClickListener for the Button
        Button112.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, login_traveler::class.java)
            startActivity(intent)
        }


        citySpinner = findViewById(R.id.place)
        nametextView = findViewById(R.id.name)
        emailEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        signup = findViewById(R.id.signupbutton)
        image = findViewById(R.id.image)
        image.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
        }

        val places = listOf("Select a place", "Gilgit", "Naran", "Kagan", "Hunza", "Skardu")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, places)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = adapter

        signup.setOnClickListener {
            val name = nametextView.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val city = citySpinner.selectedItem.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || city.isEmpty()) {
                Snackbar.make(it, "Please fill all fields", Snackbar.LENGTH_LONG).show()
            } else {
                // Register user with email and password
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            uploadtofirebase(name, email, password, city)
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(it, "Authentication failed: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

//    private fun uploadtofirebase(name: String, email: String, password: String, city: String) {
//        val image = encodedImage
//
//        // Get a reference to the Firebase Realtime Database
//        val database = FirebaseDatabase.getInstance()
//        val usersRef = database.getReference("users")
//
//        // Create a unique key for the user
//        val userId = usersRef.push().key
//
//        // Create a new User object with the provided data
//        val user = travellerdata(name, email, password, city, image)
//
//        // Upload the user data to the database under the unique key
//        if (userId != null) {
//            usersRef.child(userId).setValue(user)
//                .addOnSuccessListener {
//                    // Data successfully uploaded
//                    Snackbar.make(signup, "User data uploaded successfully", Snackbar.LENGTH_LONG).show()
//                }
//                .addOnFailureListener { e ->
//                    // Error occurred while uploading data
//                    Snackbar.make(signup, "Error uploading user data: ${e.message}", Snackbar.LENGTH_LONG).show()
//                }
//
//            // Get a reference to the Firebase Storage
//            val storageReference = FirebaseStorage.getInstance().getReference("images/${userId}")
//
//            // Convert bitmap to byte array
//            val byteArrayOutputStream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//            val data = byteArrayOutputStream.toByteArray()
//
//            // Upload the image file to Firebase Storage
//            storageReference.putBytes(data)
//                .addOnSuccessListener {
//                    // Image successfully uploaded
//                    Snackbar.make(signup, "Image uploaded successfully", Snackbar.LENGTH_LONG).show()
//                }
//                .addOnFailureListener { e ->
//                    // Error occurred while uploading image
//                    Snackbar.make(signup, "Error uploading image: ${e.message}", Snackbar.LENGTH_LONG).show()
//                }
//        }
//    }

    private fun uploadtofirebase(name: String, email: String, password: String, city: String) {
        val ref = FirebaseStorage.getInstance().getReference().child("users/" + UUID.randomUUID().toString())
        ref.putFile(fileuri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL from the task snapshot
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val image = uri.toString()
                    // Save user data to the database
                    saveUserDataToDatabase(name, email, password, city, image)
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to retrieve download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Image Upload Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserDataToDatabase(name: String, email: String, password: String, city: String, image: String) {
        // Get a reference to the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        // Create a unique key for the user
        val userId = usersRef.push().key

        // Create a new User object with the provided data
        val user = travellerdata(name, email, password, city, image)

        // Upload the user data to the database under the unique key
        if (userId != null) {
            usersRef.child(userId).setValue(user)
                .addOnSuccessListener {
                    // Data successfully uploaded
                    Snackbar.make(signup, "User data uploaded successfully", Snackbar.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    // Error occurred while uploading data
                    Snackbar.make(signup, "Error uploading user data: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileuri = data.data // Update fileuri with the selected image URI
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(fileuri!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                image.setImageBitmap(bitmap)
                encodeBitmapImage(bitmap)
            } catch (ex: Exception) {
                // Handle exception
                ex.printStackTrace()
            }
        }
    }

    private fun encodeBitmapImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytesofimage: ByteArray = byteArrayOutputStream.toByteArray()
        encodedImage = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT)
    }
}
