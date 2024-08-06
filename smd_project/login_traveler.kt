package com.laraib.smd_project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class login_traveler : AppCompatActivity() {

    private lateinit var usernametextView: EditText
    private lateinit var passwordtextView: EditText
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_traveler)

        NotificationHelper(this,"you are in Sucessfully").Notification()
        val kh = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        usernametextView = findViewById(R.id.username)
        passwordtextView = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginbutton)

        val Button112 = findViewById<TextView>(R.id.login)
        // Set OnClickListener for the Button
        Button112.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, home::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val username = usernametextView.text.toString()
            val password = passwordtextView.text.toString()

            // Authenticate user using Firebase Auth
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this, chat_traveller::class.java)
                        val intent = Intent(this, home::class.java)
                        startActivity(intent)
                        // Proceed to the next activity or perform necessary actions
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
