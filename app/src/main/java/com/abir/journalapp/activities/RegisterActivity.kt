package com.abir.journalapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abir.journalapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * RegisterActivity handles the user registration process,
 * allowing users to create a new account using their email and password.
 */
class RegisterActivity : AppCompatActivity() {

    // Binding object to interact with the layout views
    private lateinit var binding: ActivityRegisterBinding

    // Firebase Authentication instance for managing user registration
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Set up register button click listener
        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim() // Trim input to remove whitespace
            val password = binding.passwordEditText.text.toString().trim()

            // Validate that email and password fields are not empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                // Call the function to register the user
                registerUser(email, password)
            }
        }
    }

    /**
     * Registers a new user using Firebase Authentication.
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Display success message and navigate to MainActivity
                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close the RegisterActivity to prevent returning to it
                } else {
                    // Show error message if registration fails
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}