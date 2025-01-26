package com.abir.journalapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abir.journalapp.R
import com.abir.journalapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * LoginActivity handles user authentication, allowing users to log in
 * or navigate to the registration screen to create a new account.
 */
class LoginActivity : AppCompatActivity() {

    // Binding object to interact with the layout views
    private lateinit var binding: ActivityLoginBinding

    // Firebase Authentication instance for managing user login
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set up login button click listener
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim() // Trim input for better validation
            val password = binding.passwordEditText.text.toString().trim()

            // Check if email and password fields are not empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                // Call the function to log the user in
                loginUser(email, password)
            }
        }

        // Set up create account button click listener
        binding.createAccountButton.setOnClickListener {
            // Navigate to the RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out) // Add a transition effect
        }
    }

    /**
     * Logs in the user using Firebase Authentication.
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Navigate to MainActivity on successful login
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out) // Add a transition effect
                    finish() // Close the LoginActivity to prevent returning to it
                } else {
                    // Show error message if login fails
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}