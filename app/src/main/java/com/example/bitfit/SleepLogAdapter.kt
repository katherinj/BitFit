package com.example.bitfit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SleepLogAdapter(
    private val sleepLogs: MutableList<SleepLogEntity>,
    private val itemClickListener: (SleepLogEntity) -> Unit,
    private val deleteClickListener: (SleepLogEntity) -> Unit
) : RecyclerView.Adapter<SleepLogAdapter.SleepLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sleep_log, parent, false)
        return SleepLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: SleepLogViewHolder, position: Int) {
        val log = sleepLogs[position]
        holder.bind(log)
    }

    override fun getItemCount() = sleepLogs.size

    inner class SleepLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val hoursTextView: TextView = itemView.findViewById(R.id.hoursTextView)
        private val moodTextView: TextView = itemView.findViewById(R.id.moodTextView)
        private val notesTextView: TextView = itemView.findViewById(R.id.notesTextView)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(sleepLog: SleepLogEntity) {
            dateTextView.text = sleepLog.date
            hoursTextView.text = "Slept ${sleepLog.hours} hours"
            moodTextView.text = "Feeling ${sleepLog.mood}/10"
            notesTextView.text = sleepLog.notes ?: ""

            itemView.setOnClickListener {
                itemClickListener(sleepLog)
            }

            deleteButton.setOnClickListener {
                deleteClickListener(sleepLog)
            }
        }
    }
}
