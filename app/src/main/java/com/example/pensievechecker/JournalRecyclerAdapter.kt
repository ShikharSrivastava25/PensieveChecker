package com.example.pensievechecker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pensievechecker.databinding.JournalRowBinding

class JournalRecyclerAdapter(val context : Context, var journalList : List<Journal>) : RecyclerView.Adapter<JournalRecyclerAdapter.MyViewHolder>() {

    // Bind data to every individual journal entity
//    lateinit var binding : JournalRowBinding

    // View holder
//    class MyViewHolder(var binding: JournalRowBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(journal: Journal) {
//            binding.journal = journal
//        }
//    }

    class MyViewHolder(private val binding: JournalRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(journal: Journal) {
            binding.journal = journal
            binding.executePendingBindings() // Ensure bindings are updated
        }
    }

    // Responsible for inflating the views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = JournalRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    // This method returns the total number of items in the dataset, helping RecyclerView determine
    // how many items it needs to display.
    override fun getItemCount(): Int = journalList.size

    // This method binds data to the views in the ViewHolder based on the item's position in the dataset.
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val journal = journalList[position]
        holder.bind(journal)
    }
}