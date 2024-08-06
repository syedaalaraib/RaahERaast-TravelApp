package com.laraib.smd_project

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
//import android.hardware.biometrics.BiometricPrompt
//import android.hardware.biometrics.BiometricPrompt
import androidx.biometric.BiometricPrompt
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.laraib.smd_project.databinding.ActivitySignupGuideBinding

import java.io.ByteArrayOutputStream
import java.io.InputStream
import com.laraib.smd_project.travellerdata
import java.util.UUID
import java.util.concurrent.Executor
import androidx.biometric.BiometricManager



class signup_guide : AppCompatActivity() {

    private lateinit var citySpinner: Spinner
    private lateinit var nametextView: TextView
    private lateinit var emailEditText: TextView
    private lateinit var passwordEditText: TextView
    private lateinit var signup: TextView
    private lateinit var image: ImageView

    private lateinit var binding: ActivitySignupGuideBinding
    private lateinit var executar: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo




    var fileuri: Uri? = null
    private lateinit var bitmap: Bitmap
    private var encodedImage: String = ""

    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_guide)

        executar= ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this,
            executar,
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication Error: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext,
                        "Authentication Succeeded!",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext,
                        "Authentication Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

//        promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Biometric Authentication")
//            .setSubtitle("Login using fingerprint authentication")
//            .setNegativeButtonText("Cancel")
//            .build()

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Face Unlock")
            .setSubtitle("Log in using your face")
//            .setNegativeButtonText("Use Password Instead")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()





        auth = FirebaseAuth.getInstance()


        //go to the login page
        val Button112 = findViewById<TextView>(R.id.login)
        // Set OnClickListener for the Button
        Button112.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, login_guide::class.java)
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
    private fun uploadtofirebase(name: String, email: String, password: String, city: String) {
        val ref = FirebaseStorage.getInstance().getReference().child("guides/" + UUID.randomUUID().toString())
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
        val usersRef = database.getReference("guides")

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

    fun biometricAuth(view: View) {
//        biometricPrompt.authenticate(BiometricPrompt.PromptInfo)
        biometricPrompt.authenticate(promptInfo)
    }
}











//package com.laraib.smd_project
//
//import android.Manifest
//import android.app.Activity
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
////import android.hardware.biometrics.BiometricPrompt
////import android.hardware.biometrics.BiometricPrompt
//import androidx.biometric.BiometricPrompt
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.provider.MediaStore
//import android.util.Base64
//import android.view.View
//import android.widget.ArrayAdapter
//import android.widget.ImageView
//import android.widget.Spinner
//import android.widget.TextView
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.google.android.material.snackbar.Snackbar
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//import com.karumi.dexter.Dexter
//import com.laraib.smd_project.databinding.ActivitySignupGuideBinding
//
//import java.io.ByteArrayOutputStream
//import java.io.InputStream
//import com.laraib.smd_project.travellerdata
//import java.util.UUID
//import java.util.concurrent.Executor
//
//class signup_guide : AppCompatActivity() {
//
//    private lateinit var citySpinner: Spinner
//    private lateinit var nametextView: TextView
//    private lateinit var emailEditText: TextView
//    private lateinit var passwordEditText: TextView
//    private lateinit var signup: TextView
//    private lateinit var image: ImageView
//
//    private lateinit var binding: ActivitySignupGuideBinding
//    private lateinit var executar: Executor
//    private lateinit var biometricPrompt: BiometricPrompt
//    private lateinit var promptInfo: BiometricPrompt.PromptInfo
//
//
//
//
//    var fileuri: Uri? = null
//    private lateinit var bitmap: Bitmap
//    private var encodedImage: String = ""
//
//    private lateinit var auth: FirebaseAuth
//
//    @RequiresApi(Build.VERSION_CODES.P)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_signup_guide)
//
//        executar= ContextCompat.getMainExecutor(this)
//        biometricPrompt = BiometricPrompt(
//            this,
//            executar,
//            @RequiresApi(Build.VERSION_CODES.P)
//            object : BiometricPrompt.AuthenticationCallback() {
//                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//                    super.onAuthenticationError(errorCode, errString)
//                    Toast.makeText(
//                        applicationContext,
//                        "Authentication Error: $errString",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                    super.onAuthenticationSucceeded(result)
//                    Toast.makeText(
//                        applicationContext,
//                        "Authentication Succeeded!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//
//                }
//
//                override fun onAuthenticationFailed() {
//                    super.onAuthenticationFailed()
//                    Toast.makeText(
//                        applicationContext,
//                        "Authentication Failed",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })
//
////        promptInfo = BiometricPrompt.PromptInfo.Builder()
////            .setTitle("Biometric Authentication")
////            .setSubtitle("Login using fingerprint authentication")
////            .setNegativeButtonText("Cancel")
////            .build()
//
//        promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Biometric Authentication")
//            .setSubtitle("Log in using your fingerprint")
//            .setNegativeButtonText("Cancel")
//            .build()
//
//
//
//
//
//        auth = FirebaseAuth.getInstance()
//
//
//        //go to the login page
//        val Button112 = findViewById<TextView>(R.id.login)
//        // Set OnClickListener for the Button
//        Button112.setOnClickListener {
//            // Navigate to a new page here
//            val intent = Intent(this, login_guide::class.java)
//            startActivity(intent)
//        }
//
//
//        citySpinner = findViewById(R.id.place)
//        nametextView = findViewById(R.id.name)
//        emailEditText = findViewById(R.id.username)
//        passwordEditText = findViewById(R.id.password)
//        signup = findViewById(R.id.signupbutton)
//        image = findViewById(R.id.image)
//        image.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
//        }
//
//        val places = listOf("Select a place", "Gilgit", "Naran", "Kagan", "Hunza", "Skardu")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, places)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        citySpinner.adapter = adapter
//
//        signup.setOnClickListener {
//            val name = nametextView.text.toString()
//            val email = emailEditText.text.toString()
//            val password = passwordEditText.text.toString()
//            val city = citySpinner.selectedItem.toString()
//
//            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || city.isEmpty()) {
//                Snackbar.make(it, "Please fill all fields", Snackbar.LENGTH_LONG).show()
//            } else {
//                // Register user with email and password
//                auth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this) { task ->
//                        if (task.isSuccessful) {
//                            // Sign in success, update UI with the signed-in user's information
//                            val user = auth.currentUser
//                            uploadtofirebase(name, email, password, city)
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Snackbar.make(it, "Authentication failed: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
//                        }
//                    }
//            }
//        }
//    }
//    private fun uploadtofirebase(name: String, email: String, password: String, city: String) {
//        val ref = FirebaseStorage.getInstance().getReference().child("guides/" + UUID.randomUUID().toString())
//        ref.putFile(fileuri!!)
//            .addOnSuccessListener { taskSnapshot ->
//                // Get the download URL from the task snapshot
//                ref.downloadUrl.addOnSuccessListener { uri ->
//                    val image = uri.toString()
//                    // Save user data to the database
//                    saveUserDataToDatabase(name, email, password, city, image)
//                }.addOnFailureListener { exception ->
//                    Toast.makeText(this, "Failed to retrieve download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
//                }
//            }.addOnFailureListener { exception ->
//                Toast.makeText(this, "Image Upload Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun saveUserDataToDatabase(name: String, email: String, password: String, city: String, image: String) {
//        // Get a reference to the Firebase Realtime Database
//        val database = FirebaseDatabase.getInstance()
//        val usersRef = database.getReference("guides")
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
//        }
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//            fileuri = data.data // Update fileuri with the selected image URI
//            try {
//                val inputStream: InputStream? = contentResolver.openInputStream(fileuri!!)
//                bitmap = BitmapFactory.decodeStream(inputStream)
//                image.setImageBitmap(bitmap)
//                encodeBitmapImage(bitmap)
//            } catch (ex: Exception) {
//                // Handle exception
//                ex.printStackTrace()
//            }
//        }
//    }
//
//    private fun encodeBitmapImage(bitmap: Bitmap) {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//        val bytesofimage: ByteArray = byteArrayOutputStream.toByteArray()
//        encodedImage = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT)
//    }
//
//    fun biometricAuth(view: View) {
////        biometricPrompt.authenticate(BiometricPrompt.PromptInfo)
//        biometricPrompt.authenticate(promptInfo)
//    }
//}
