package com.abir.journalapp.utils

import com.abir.journalapp.models.JournalEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * A utility object to interact with Firebase Firestore for managing journal entries.
 */
object FirebaseHelper {

    private val firestore = FirebaseFirestore.getInstance()
    private val journalCollection = firestore.collection("journals")

    /**
     * Generate a new unique entry ID for a journal entry.
     *
     * @return A unique document ID.
     */
    fun getNewEntryId(): String = journalCollection.document().id

    /**
     * Add a new journal entry or update an existing one for a specific user.
     *
     * @param entry The journal entry to be added or updated.
     * @param userId The ID of the user who owns the journal entry.
     * @param onSuccess Callback function invoked on successful operation.
     * @param onFailure Callback function invoked on failure with the exception.
     */
    fun addOrUpdateEntry(
        entry: JournalEntry,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val entryWithUserId = entry.copy(userId = userId) // Associate the entry with the user ID
        journalCollection.document(entry.id)
            .set(entryWithUserId) // Use set() to create or update the document
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    /**
     * Delete a journal entry from Firestore.
     *
     * @param entryId The ID of the journal entry to be deleted.
     * @param onSuccess Callback function invoked on successful deletion.
     * @param onFailure Callback function invoked on failure with the exception.
     */
    fun deleteEntry(
        entryId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        journalCollection.document(entryId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    /**
     * Fetch all journal entries for a specific user.
     *
     * @param userId The ID of the user whose journal entries are to be fetched.
     * @param onSuccess Callback function invoked with the list of journal entries on success.
     * @param onFailure Callback function invoked on failure with the exception.
     */
    fun getAllEntries(
        userId: String,
        onSuccess: (List<JournalEntry>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        journalCollection.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { snapshot ->
                val entries = snapshot.map { doc ->
                    doc.toObject(JournalEntry::class.java).apply {
                        id = doc.id // Ensure the ID is populated for editing or deleting
                    }
                }
                onSuccess(entries)
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    /**
     * Real-time listener for journal entries of a specific user.
     *
     * @param userId The ID of the user whose journal entries are being monitored.
     * @param onUpdate Callback function invoked with the list of updated journal entries on success.
     * @param onError Callback function invoked on failure with the exception.
     * @return A ListenerRegistration object to remove the listener when no longer needed.
     */
    fun listenForEntries(
        userId: String,
        onUpdate: (List<JournalEntry>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return journalCollection.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    onError(exception)
                    return@addSnapshotListener
                }

                val entries = snapshot?.toObjects(JournalEntry::class.java) ?: emptyList()
                onUpdate(entries)
            }
    }
}