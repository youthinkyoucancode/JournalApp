package com.abir.journalapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abir.journalapp.databinding.ItemJournalEntryBinding
import com.abir.journalapp.models.JournalEntry

/**
 * Adapter class for managing the display of journal entries in a RecyclerView.
 *
 * @param onDeleteClicked Callback function to handle delete button clicks.
 * @param onEditClicked Callback function to handle clicks for editing entries.
 */
class JournalEntryAdapter(
    private val onDeleteClicked: (JournalEntry) -> Unit,
    private val onEditClicked: (JournalEntry) -> Unit
) : RecyclerView.Adapter<JournalEntryAdapter.ViewHolder>() {

    // List of journal entries displayed in the RecyclerView
    private var entries = listOf<JournalEntry>()

    /**
     * Updates the list of journal entries and notifies the RecyclerView of the data change.
     *
     * @param list The new list of journal entries.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<JournalEntry>) {
        entries = list
        notifyDataSetChanged() // Refresh the RecyclerView
    }

    /**
     * Creates a new ViewHolder instance for the RecyclerView.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemJournalEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    /**
     * Returns the total number of items in the RecyclerView.
     *
     * @return The size of the entries list.
     */
    override fun getItemCount() = entries.size

    /**
     * Binds data to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    /**
     * ViewHolder class to hold and bind views for each journal entry item.
     *
     * @param binding The view binding object for the item layout.
     */
    inner class ViewHolder(private val binding: ItemJournalEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a journal entry to the item view.
         *
         * @param entry The journal entry to bind.
         */
        fun bind(entry: JournalEntry) {
            // Set the title and content of the journal entry
            binding.textViewTitle.text = entry.title
            binding.textViewContent.text = entry.content

            // Set click listeners for delete and edit actions
            binding.buttonDelete.setOnClickListener { onDeleteClicked(entry) }
            binding.root.setOnClickListener { onEditClicked(entry) }
        }
    }
}