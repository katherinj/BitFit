package com.example.bitfit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepLogDao {
    @Insert
    fun insertSleepLog(sleepLog: SleepLogEntity)

    @Query("SELECT * FROM sleep_logs ORDER BY id DESC")
    fun getAllSleepLogs(): Flow<List<SleepLogEntity>>

    @Query("DELETE FROM sleep_logs WHERE id = :logId")
    fun deleteSleepLog(logId: Int)

    @Query("DELETE FROM sleep_logs")
    fun deleteAll()

    @Update
    fun updateSleepLog(sleepLog: SleepLogEntity)

}