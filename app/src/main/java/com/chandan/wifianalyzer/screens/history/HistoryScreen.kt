package com.chandan.wifianalyzer.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chandan.wifianalyzer.data.database.AppDatabase
import com.chandan.wifianalyzer.data.repository.SignalMeasurementRepository
import com.chandan.wifianalyzer.viewmodel.SignalViewModel
import com.chandan.wifianalyzer.viewmodel.SignalViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen() {

    val context = LocalContext.current

    val database = remember {
        AppDatabase.getDatabase(context)
    }

    val repository = remember {
        SignalMeasurementRepository(
            database.signalMeasurementDao()
        )
    }

    val viewModel: SignalViewModel = viewModel(
        factory = SignalViewModelFactory(repository)
    )

    val measurements by viewModel.measurements.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                text = "Measurement History",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        if (measurements.isEmpty()) {

            item {
                Text(
                    text = "No measurements saved yet.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        } else {

            items(measurements) { measurement ->

                MeasurementCard(
                    corner = measurement.corner,
                    ssid = measurement.ssid,
                    rssi = measurement.rssi,
                    quality = measurement.signalQuality,
                    timestamp = measurement.timestamp
                )
            }
        }
    }
}

@Composable
private fun MeasurementCard(
    corner: Int,
    ssid: String,
    rssi: Int,
    quality: Int,
    timestamp: Long
) {

    val dateFormat = SimpleDateFormat(
        "dd MMM yyyy, hh:mm a",
        Locale.getDefault()
    )

    val formattedDate = dateFormat.format(
        Date(timestamp)
    )

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = "Corner $corner",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Wi-Fi: $ssid"
            )

            Text(
                text = "RSSI: $rssi dBm"
            )

            Text(
                text = "Signal Quality: $quality%"
            )

            Text(
                text = "Measured: $formattedDate",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}