package com.example.bitfit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bitfit.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

private const val TAG = "MainActivity/"

class MainActivity : AppCompatActivity() {
    private val sleepLogs = mutableListOf<SleepLogEntity>()
    private lateinit var sleepRecyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: SleepLogAdapter

    private var selectedLog: SleepLogEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = AppDatabase.getInstance(this)

        setupRecyclerView()

        loadSleepLogs()

        binding.saveButton.setOnClickListener {
            saveSleepData()
        }
    }

    private fun setupRecyclerView() {
        adapter = SleepLogAdapter(sleepLogs) { sleepLog ->
            selectedLog = sleepLog
            binding.inputSection.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE

            binding.sleepSlider.value = sleepLog.hours ?: 0f
            binding.moodSlider.value = sleepLog.mood?.toFloat() ?: 5f
            binding.notesInput.setText(sleepLog.notes)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun saveSleepData() {
        val sleepHours = binding.sleepSlider.value
        val moodRating = binding.moodSlider.value.toInt()
        val notes = binding.notesInput.text.toString()
        val date = "MAR 5 2025"

        lifecycleScope.launch(IO) {
            if (selectedLog != null) {
                val updatedLog = selectedLog!!.copy(hours = sleepHours, mood = moodRating, notes = notes)
                database.sleepLogDao().updateSleepLog(updatedLog)
            } else {
                val newLog = SleepLogEntity(date = date, hours = sleepHours, mood = moodRating, notes = notes)
                database.sleepLogDao().insertSleepLog(newLog)
            }

            selectedLog = null

            runOnUiThread {
                binding.inputSection.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }

            loadSleepLogs()
        }
    }

    private fun loadSleepLogs() {
        lifecycleScope.launch {
            database.sleepLogDao().getAllSleepLogs().collect { databaseList ->
                runOnUiThread {
                    sleepLogs.clear()
                    sleepLogs.addAll(databaseList)
                    adapter.notifyDataSetChanged()

                    updateAverages()

                    if (sleepLogs.isNotEmpty()) {
                        binding.recyclerView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun updateAverages() {
        if (sleepLogs.isNotEmpty()) {
            val totalSleep = sleepLogs.sumOf { it.hours?.toDouble() ?: 0.0 }
            val totalMood = sleepLogs.sumOf { it.mood?.toDouble() ?: 0.0 }
            val count = sleepLogs.size

            val avgSleep = totalSleep / count
            val avgMood = totalMood / count

            binding.averageSleepText.text = "Average hours of sleep: %.1f hours".format(avgSleep)
            binding.averageMoodText.text = "Average feeling: %.1f / 10".format(avgMood)
        } else {
            binding.averageSleepText.text = "Average hours of sleep: 0.0 hours"
            binding.averageMoodText.text = "Average feeling: 0.0 / 10"
        }
    }
}
