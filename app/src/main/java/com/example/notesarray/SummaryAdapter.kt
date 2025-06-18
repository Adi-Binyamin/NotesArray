package com.example.notesarray

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notesarray.data.Summary

class SummaryAdapter(
    private val summaries: List<Summary>,
    private val itemClickListener: (Summary) -> Unit
) : RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>() {

    inner class SummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val summaryText: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(summary: Summary, position: Int) {
            summaryText.text = "${position + 1}. ${summary.name}"
            itemView.setOnClickListener {
                itemClickListener(summary)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return SummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        holder.bind(summaries[position], position)
    }


    override fun getItemCount() = summaries.size
}
