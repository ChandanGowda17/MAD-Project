package com.chandan.wifianalyzer.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chandan.wifianalyzer.data.model.SignalMeasurement
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalMeasurementDao {

    @Insert
    suspend fun insertMeasurement(
        measurement: SignalMeasurement
    )

    @Query("SELECT * FROM signal_measurements ORDER BY timestamp DESC")
    fun getAllMeasurements(): Flow<List<SignalMeasurement>>

    @Query("DELETE FROM signal_measurements")
    suspend fun deleteAllMeasurements()
}