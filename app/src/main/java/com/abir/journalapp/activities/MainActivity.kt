package com.abir.journalapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abir.journalapp.adapters.JournalEntryAdapter
import com.abir.journalapp.databinding.ActivityMainBinding
import com.abir.journalapp.models.JournalEntry
import com.abir.journalapp.utils.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

/**
 * MainActivity handles the display of the user's journal entries and their interactions.
 * It also includes functionality for adding, editing, deleting entries, and logging out.
 */
class MainActivity : AppCompatActivity() {

    // Binding for the activity's layout
    private lateinit var binding: ActivityMainBinding

    // Adapter for the RecyclerView to display journal entries
    private lateinit var adapter: JournalEntryAdapter

    // Firebase authentication instance to manage the logged-in user
    private lateinit var auth: FirebaseAuth

    // Listener for real-time updates from Firestore
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Check if the user is logged in; if not, navigate to the login screen
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            navigateToLoginScreen()
            return
        }

        // Set up RecyclerView with a LinearLayoutManager and adapter
        adapter = JournalEntryAdapter(
            onEditClicked = { entry -> editEntry(entry) }, // Handle edit action
            onDeleteClicked = { entry -> deleteEntry(entry) } // Handle delete action
        )
        binding.recyclerViewEntries.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewEntries.adapter = adapter

        // Start listening for journal entries belonging to the current user
        startListeningForEntries(currentUserId)

        // Add entry button click listener
        binding.fabAddEntry.setOnClickListener {
            val intent = Intent(this, AddEditEntryActivity::class.java)
            startActivity(intent)
        }

        // Logout button click listener
        binding.logoutButton.setOnClickListener {
            auth.signOut() // Sign out the user
            navigateToLoginScreen() // Navigate back to login screen
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove Firestore listener to prevent memory leaks
        listenerRegistration?.remove()
    }

    /**
     * Navigates the user to the LoginActivity and finishes the current activity.
     */
    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Sets up a real-time listener for the user's journal entries in Firestore.
     *
     * @param userId The ID of the logged-in user whose entries to listen for.
     */
    private fun startListeningForEntries(userId: String) {
        listenerRegistration = FirebaseHelper.listenForEntries(
            userId = userId,
            onUpdate = { entries ->
                if (entries.isEmpty()) {
                    // Show empty state if no entries exist
                    binding.recyclerViewEntries.visibility = View.GONE
                    binding.textViewEmptyState.visibility = View.VISIBLE
                } else {
                    // Populate the RecyclerView with the fetched entries
                    binding.recyclerViewEntries.visibility = View.VISIBLE
                    binding.textViewEmptyState.visibility = View.GONE
                    adapter.submitList(entries)
                }
            },
            onError = { exception ->
                // Display an error message if fetching entries fails
                Toast.makeText(this, "Failed to load entries: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    /**
     * Launches AddEditEntryActivity to edit an existing journal entry.
     *
     * @param entry The journal entry to be edited.
     */
    private fun editEntry(entry: JournalEntry) {
        val intent = Intent(this, AddEditEntryActivity::class.java).apply {
            putExtra("entryId", entry.id)
            putExtra("entryTitle", entry.title)
            putExtra("entryContent", entry.content)
        }
        startActivity(intent)
    }

    /**
     * Deletes a journal entry from Firestore.
     *
     * @param entry The journal entry to be deleted.
     */
    private fun deleteEntry(entry: JournalEntry) {
        FirebaseHelper.deleteEntry(
            entryId = entry.id,
            onSuccess = {
                Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()
            },
            onFailure = { exception ->
                Toast.makeText(this, "Failed to delete entry: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}