package com.chandan.wifianalyzer.screens.signal

import androidx.lifecycle.viewmodel.compose.viewModel
import com.chandan.wifianalyzer.data.database.AppDatabase
import com.chandan.wifianalyzer.data.model.SignalMeasurement
import com.chandan.wifianalyzer.data.repository.SignalMeasurementRepository
import com.chandan.wifianalyzer.viewmodel.SignalViewModel
import com.chandan.wifianalyzer.viewmodel.SignalViewModelFactory

import androidx.activity.compose.rememberLauncherForActivityResult
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.chandan.wifianalyzer.utils.WifiDetails
import com.chandan.wifianalyzer.utils.WifiUtils

@Composable
fun SignalScreen() {

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

    val cornerReadings = remember {
        mutableStateMapOf<Int, WifiDetails?>(
            1 to null,
            2 to null,
            3 to null,
            4 to null
        )
    }

    var selectedCorner by remember {
        mutableStateOf<Int?>(null)
    }

    var wifiName by remember {
        mutableStateOf("Not connected")
    }

    var message by remember {
        mutableStateOf("")
    }

    var isScanning by remember {
        mutableStateOf(false)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts
                .RequestMultiplePermissions()
        ) { permissions ->

            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {

                selectedCorner?.let {
                    startWifiScan(
                        context = context,
                        corner = it,
                        onScanning = {
                            isScanning = true
                            message = "Scanning Wi-Fi..."
                        }
                    )
                }

            } else {

                message = "Location permission is required."
            }
        }

    DisposableEffect(context) {

        val receiver = object : BroadcastReceiver() {

            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {

                if (intent?.action ==
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
                ) {

                    val corner = selectedCorner

                    if (corner != null) {

                        val details =
                            WifiUtils.getCurrentWifiDetails(context!!)

                        if (details != null) {

                            cornerReadings[corner] = details

                            wifiName = details.ssid

                            val measurement = SignalMeasurement(
                                corner = corner,
                                ssid = details.ssid,
                                rssi = details.rssi,
                                signalQuality = details.signalQuality,
                                frequency = details.frequency,
                                linkSpeed = details.linkSpeed,
                                timestamp = System.currentTimeMillis()
                            )

                            viewModel.saveMeasurement(measurement)

                            message =
                                "Corner $corner captured and saved."

                        } else {

                            message =
                                "Could not read Wi-Fi information."
                        }

                        isScanning = false
                    }
                }
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
            ),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    fun captureSignal(corner: Int) {

        selectedCorner = corner

        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {

            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            return
        }

        startWifiScan(
            context = context,
            corner = corner,
            onScanning = {
                isScanning = true
                message = "Scanning Wi-Fi..."
            }
        )
    }

    val bestCorner =
        cornerReadings
            .filterValues { it != null }
            .maxByOrNull {
                it.value?.rssi ?: Int.MIN_VALUE
            }
            ?.key

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {

            Text(
                text = "Wi-Fi Signal Strength",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {

            Text(
                text = "Connected Wi-Fi: $wifiName"
            )
        }

        if (message.isNotEmpty()) {

            item {

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (isScanning) {

            item {

                Text(
                    text = "Please wait for the Wi-Fi scan..."
                )
            }
        }

        items(listOf(1, 2, 3, 4)) { corner ->

            CornerCard(
                corner = corner,
                details = cornerReadings[corner],
                onCapture = {
                    captureSignal(corner)
                }
            )
        }

        item {
            SignalGraph(
                readings = (1..4).associateWith { corner ->
                    cornerReadings[corner]?.rssi
                }
            )
        }

        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Best Location",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = if (bestCorner != null) {
                            "Corner $bestCorner"
                        } else {
                            "Waiting for scans..."
                        },
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}

private fun startWifiScan(
    context: Context,
    corner: Int,
    onScanning: () -> Unit
) {

    onScanning()

    WifiUtils.requestWifiScan(context)
}

@Composable
private fun CornerCard(
    corner: Int,
    details: WifiDetails?,
    onCapture: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Corner $corner",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = if (details != null) {
                    "RSSI: ${details.rssi} dBm"
                } else {
                    "RSSI: --"
                }
            )

            Text(
                text = if (details != null) {
                    "Quality: ${details.signalQuality}%"
                } else {
                    "Quality: --"
                }
            )

            Button(
                onClick = onCapture,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Capture Signal"
                )
            }
        }
    }
}