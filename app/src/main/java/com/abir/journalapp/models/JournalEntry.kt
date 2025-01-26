package com.abir.journalapp.models

/**
 * Data class representing a journal entry.
 * Each journal entry belongs to a specific user and contains details such as title, content, and metadata.
 *
 * @property id A unique identifier for the journal entry. Default is an empty string.
 * @property title The title of the journal entry. Default is an empty string.
 * @property content The main content of the journal entry. Default is an empty string.
 * @property userId The ID of the user who created this journal entry. Default is an empty string.
 * @property createdAt The timestamp (in milliseconds) when the entry was created. Defaults to the current system time.
 */
data class JournalEntry(
    var id: String = "",                // Unique identifier for the journal entry
    val title: String = "",             // Title of the journal entry
    val content: String = "",           // Content or body of the journal entry
    val userId: String = "",            // ID of the user who owns the entry
    val createdAt: Long = System.currentTimeMillis() // Timestamp for creation (default: current system time)
)