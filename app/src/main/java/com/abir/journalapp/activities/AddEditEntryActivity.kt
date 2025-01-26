package com.abir.journalapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abir.journalapp.R
import com.abir.journalapp.databinding.ActivityAddEditEntryBinding
import com.abir.journalapp.models.JournalEntry
import com.abir.journalapp.utils.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth

/**
 * AddEditEntryActivity is responsible for handling the creation and editing of journal entries.
 * It allows the user to add a new journal entry or modify an existing one.
 */
class AddEditEntryActivity : AppCompatActivity() {

    // Binding object to interact with the layout views
    private lateinit var binding: ActivityAddEditEntryBinding

    // Entry ID for the journal entry being edited (null for new entries)
    private var entryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityAddEditEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve entry details (if any) passed through the intent
        entryId = intent.getStringExtra("entryId")
        val title = intent.getStringExtra("entryTitle")
        val content = intent.getStringExtra("entryContent")

        // Populate the fields if editing an existing entry
        if (!entryId.isNullOrEmpty()) {
            binding.editTextTitle.setText(title)
            binding.editTextContent.setText(content)
        }

        // Set up Save button click listener
        binding.buttonSave.setOnClickListener {
            val newTitle = binding.editTextTitle.text.toString().trim()
            val newContent = binding.editTextContent.text.toString().trim()

            // Ensure that both title and content are not empty
            if (newTitle.isEmpty() || newContent.isEmpty()) {
                Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get the current user ID from Firebase Authentication
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new JournalEntry object
            val newEntry = JournalEntry(
                id = entryId ?: FirebaseHelper.getNewEntryId(), // Generate a new ID if this is a new entry
                title = newTitle,
                content = newContent
            )

            // Save the entry to Firestore
            saveEntry(newEntry, currentUserId)
        }
    }

    /**
     * Saves the journal entry to Firestore.
     *
     * @param entry The journal entry to save (new or updated).
     * @param userId The ID of the currently logged-in user.
     */
    private fun saveEntry(entry: JournalEntry, userId: String) {
        FirebaseHelper.addOrUpdateEntry(
            entry = entry,
            userId = userId,
            onSuccess = {
                Toast.makeText(this, "Entry saved successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after saving
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out) // Add transition effect
            },
            onFailure = { exception ->
                Toast.makeText(this, "Failed to save entry: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}