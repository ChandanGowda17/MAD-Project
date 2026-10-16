package com.chandan.wifianalyzer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signal_measurements")
data class SignalMeasurement(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val corner: Int,

    val ssid: String,

    val rssi: Int,

    val signalQuality: Int,

    val frequency: Int,

    val linkSpeed: Int,

    val timestamp: Long
)