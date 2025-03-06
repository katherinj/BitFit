package com.example.bitfit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SleepLogAdapter(
    private var sleepLogs: List<SleepLogEntity>,
    private val onItemClick: (SleepLogEntity) -> Unit
) : RecyclerView.Adapter<SleepLogAdapter.SleepLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepLogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sleep_log, parent, false)
        return SleepLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: SleepLogViewHolder, position: Int) {
        val sleepLog = sleepLogs[position]
        holder.date.text = sleepLog.date
        holder.hours.text = "Slept %.1f hours".format(sleepLog.hours)
        holder.mood.text = "Feeling ${sleepLog.mood}/10"
        holder.notes.text = sleepLog.notes

        holder.itemView.setOnClickListener {
            onItemClick(sleepLog)
        }
    }

    override fun getItemCount(): Int = sleepLogs.size

    fun updateData(newLogs: List<SleepLogEntity>) {
        sleepLogs = newLogs
        notifyDataSetChanged()
    }

    class SleepLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val hours: TextView = itemView.findViewById(R.id.hoursTextView)
        val mood: TextView = itemView.findViewById(R.id.moodTextView)
        val notes: TextView = itemView.findViewById(R.id.notesTextView)
    }
}
