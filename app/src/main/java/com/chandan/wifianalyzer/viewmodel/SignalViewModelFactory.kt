package com.chandan.wifianalyzer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chandan.wifianalyzer.data.repository.SignalMeasurementRepository

class SignalViewModelFactory(
    private val repository: SignalMeasurementRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(SignalViewModel::class.java)) {
            return SignalViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}