package com.chandan.wifianalyzer.data.repository

import com.chandan.wifianalyzer.data.database.SignalMeasurementDao
import com.chandan.wifianalyzer.data.model.SignalMeasurement
import kotlinx.coroutines.flow.Flow

class SignalMeasurementRepository(
    private val dao: SignalMeasurementDao
) {

    suspend fun insertMeasurement(
        measurement: SignalMeasurement
    ) {
        dao.insertMeasurement(measurement)
    }

    fun getAllMeasurements(): Flow<List<SignalMeasurement>> {
        return dao.getAllMeasurements()
    }

    suspend fun deleteAllMeasurements() {
        dao.deleteAllMeasurements()
    }
}