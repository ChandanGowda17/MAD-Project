package com.chandan.wifianalyzer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.wifianalyzer.data.model.SignalMeasurement
import com.chandan.wifianalyzer.data.repository.SignalMeasurementRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SignalViewModel(
    private val repository: SignalMeasurementRepository
) : ViewModel() {

    val measurements: StateFlow<List<SignalMeasurement>> =
        repository.getAllMeasurements()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun saveMeasurement(
        measurement: SignalMeasurement
    ) {
        viewModelScope.launch {
            repository.insertMeasurement(measurement)
        }
    }

    fun deleteAllMeasurements() {
        viewModelScope.launch {
            repository.deleteAllMeasurements()
        }
    }
}