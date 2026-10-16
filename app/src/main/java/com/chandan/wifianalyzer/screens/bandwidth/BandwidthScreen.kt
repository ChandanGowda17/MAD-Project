
package com.chandan.wifianalyzer.screens.bandwidth

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
import com.chandan.wifianalyzer.utils.SpeedTestUtils

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@Composable
fun BandwidthScreen() {
    val context = LocalContext.current

    var downloadSpeed by remember { mutableStateOf("--") }
    var uploadSpeed by remember { mutableStateOf("--") }
    var ping by remember { mutableStateOf("--") }

    var isTesting by remember { mutableStateOf(false) }
    var testMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasLocationPermission = granted
        }

    var networkType by remember { mutableStateOf("Checking...") }
    var connectionStatus by remember { mutableStateOf("Checking...") }
    var ssid by remember { mutableStateOf("--") }
    var frequency by remember { mutableStateOf("--") }
    var linkSpeed by remember { mutableStateOf("--") }
    var rssi by remember { mutableStateOf("--") }

    fun refreshNetworkInfo() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities =
            connectivityManager.getNetworkCapabilities(network)

        connectionStatus =
            if (capabilities != null) "Connected" else "Disconnected"

        networkType = when {
            capabilities?.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            ) == true -> "Wi-Fi"

            capabilities?.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) == true -> "Mobile data"

            capabilities?.hasTransport(
                NetworkCapabilities.TRANSPORT_ETHERNET
            ) == true -> "Ethernet"

            else -> "Unknown"
        }

        if (networkType == "Wi-Fi") {
            val wifiManager =
                context.applicationContext.getSystemService(
                    Context.WIFI_SERVICE
                ) as WifiManager

            @Suppress("DEPRECATION")
            val info = wifiManager.connectionInfo

            val connectedSsid = info.ssid
                .removeSurrounding("\"")

            ssid = if (
                connectedSsid.isNotBlank() &&
                connectedSsid != "<unknown ssid>"
            ) {
                connectedSsid
            } else {
                "Unavailable"
            }

            frequency = if (info.frequency > 0) {
                "${info.frequency} MHz"
            } else {
                "Unavailable"
            }

            linkSpeed = if (info.linkSpeed > 0) {
                "${info.linkSpeed} Mbps"
            } else {
                "Unavailable"
            }

            rssi = if (info.rssi in -100..0) {
                "${info.rssi} dBm"
            } else {
                "Unavailable"
            }
        } else {
            ssid = "Not using Wi-Fi"
            frequency = "--"
            linkSpeed = "--"
            rssi = "--"
        }
    }

    LaunchedEffect(Unit) {
        refreshNetworkInfo()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Bandwidth Detector",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            "Monitor your network connection and performance.",
            style = MaterialTheme.typography.bodyMedium
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Connection Status",
                    style = MaterialTheme.typography.titleLarge
                )
                Text("Status: $connectionStatus")
                Text("Network type: $networkType")
                Text("Wi-Fi name: $ssid")
                Text("Frequency: $frequency")
                Text("Link speed: $linkSpeed")
                Text("Signal strength: $rssi")
            }
        }

        Text(
            "Internet Speed Test",
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SpeedCard(
                title = "Download",
                value = downloadSpeed,
                unit = "Mbps",
                modifier = Modifier.weight(1f)
            )

            SpeedCard(
                title = "Upload",
                value = uploadSpeed,
                unit = "Mbps",
                modifier = Modifier.weight(1f)
            )
        }

        SpeedCard(
            title = "Ping",
            value = ping,
            unit = "ms",
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    isTesting = true
                    testMessage = "Testing connection..."
                    downloadSpeed = "--"
                    uploadSpeed = "--"
                    ping = "--"

                    try {
                        val result = SpeedTestUtils.runTest()

                        downloadSpeed =
                            "%.2f".format(result.downloadMbps)

                        uploadSpeed =
                            "%.2f".format(result.uploadMbps)

                        ping = result.pingMs.toString()

                        testMessage = "Speed test completed."
                    } catch (e: Exception) {
                        testMessage =
                            "Test failed: ${e.localizedMessage ?: "Check your internet connection."}"
                    } finally {
                        isTesting = false
                    }
                }
            },
            enabled = !isTesting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isTesting) "Testing..." else "Start Speed Test")
        }

        if (isTesting) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (testMessage.isNotEmpty()) {
            Text(
                text = testMessage,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = { refreshNetworkInfo() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.NetworkWifi,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text("Refresh Network Info")
        }

        Text(
            "Download, upload and ping testing will be implemented next. " +
                    "Wi-Fi link speed is not the same as actual internet speed.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SpeedCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.width(6.dp))
                Text(unit, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
