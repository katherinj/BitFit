package com.example.bitfit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitfit.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: SleepLogAdapter
    private val sleepLogs = mutableListOf<SleepLogEntity>()
    private var selectedLog: SleepLogEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getInstance(this)

        setupRecyclerView()
        loadSleepLogs()

        binding.dateEditText.setText(getCurrentDate())

        binding.dateEditText.setOnClickListener {
            showDatePicker()
        }

        binding.saveButton.setOnClickListener {
            saveSleepData()
        }
    }

    private fun setupRecyclerView() {
        adapter = SleepLogAdapter(sleepLogs,
            { sleepLog ->
                selectedLog = sleepLog

                binding.dateEditText.setText(sleepLog.date)
                binding.sleepSlider.value = sleepLog.hours
                binding.moodSlider.value = sleepLog.mood.toFloat()
                binding.notesInput.setText(sleepLog.notes)
            },
            { sleepLog ->
                deleteSleepLog(sleepLog)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }


    private fun saveSleepData() {
        val sleepHours = binding.sleepSlider.value
        val moodRating = binding.moodSlider.value.toInt()
        val notes = binding.notesInput.text.toString()

        val selectedDate = binding.dateEditText.text.toString().ifEmpty { getCurrentDate() }

        lifecycleScope.launch(IO) {
            if (selectedLog != null) {
                val updatedLog = selectedLog!!.copy(
                    date = selectedDate,
                    hours = sleepHours,
                    mood = moodRating,
                    notes = notes
                )
                database.sleepLogDao().updateSleepLog(updatedLog)
            } else {
                val newLog = SleepLogEntity(date = selectedDate, hours = sleepHours, mood = moodRating, notes = notes)
                database.sleepLogDao().insertSleepLog(newLog)
            }

            selectedLog = null

            runOnUiThread { resetInputFields() }
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
                }
            }
        }
    }

    private fun updateAverages() {
        if (sleepLogs.isNotEmpty()) {
            val totalSleep = sleepLogs.sumOf { it.hours.toDouble() }
            val totalMood = sleepLogs.sumOf { it.mood.toDouble() }
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

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "${selectedMonth + 1}/$selectedDay/$selectedYear"
            binding.dateEditText.setText(formattedDate)
        }, year, month, day)

        datePicker.show()
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun resetInputFields() {
        binding.dateEditText.setText(getCurrentDate())
        binding.sleepSlider.value = 6.0f
        binding.moodSlider.value = 5f
        binding.notesInput.text.clear()
    }

    private fun deleteSleepLog(sleepLog: SleepLogEntity) {
        lifecycleScope.launch(IO) {
            database.sleepLogDao().deleteSleepLog(sleepLog.id)

            runOnUiThread {
                sleepLogs.remove(sleepLog)
                adapter.notifyDataSetChanged()
            }
        }
    }


}
