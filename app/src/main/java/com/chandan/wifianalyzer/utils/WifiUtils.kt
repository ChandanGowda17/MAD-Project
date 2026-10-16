package com.chandan.wifianalyzer.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat

data class WifiDetails(
    val ssid: String,
    val rssi: Int,
    val frequency: Int,
    val linkSpeed: Int,
    val signalQuality: Int
)

object WifiUtils {

    fun getWifiManager(context: Context): WifiManager {
        return context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    fun getCurrentWifiDetails(context: Context): WifiDetails? {

        if (!hasLocationPermission(context)) {
            return null
        }

        val wifiManager = getWifiManager(context)

        if (!wifiManager.isWifiEnabled) {
            return null
        }

        val connectionInfo = wifiManager.connectionInfo
        val connectedSsid = connectionInfo.ssid
            .removeSurrounding("\"")

        if (connectedSsid.isEmpty() || connectedSsid == "<unknown ssid>") {
            return null
        }

        /*
         * Use the latest scan results rather than relying only
         * on WifiInfo.rssi.
         */
        val scanResults = wifiManager.scanResults

        val connectedNetwork = scanResults
            .filter { result ->
                result.SSID == connectedSsid
            }
            .maxByOrNull { result ->
                result.level
            }

        val rssi = connectedNetwork?.level
            ?: connectionInfo.rssi

        val frequency = connectedNetwork?.frequency
            ?: connectionInfo.frequency

        val linkSpeed = connectionInfo.linkSpeed

        return WifiDetails(
            ssid = connectedSsid,
            rssi = rssi,
            frequency = frequency,
            linkSpeed = linkSpeed,
            signalQuality = calculateSignalQuality(rssi)
        )
    }

    fun requestWifiScan(context: Context): Boolean {

        if (!hasLocationPermission(context)) {
            return false
        }

        val wifiManager = getWifiManager(context)

        if (!wifiManager.isWifiEnabled) {
            return false
        }

        return wifiManager.startScan()
    }

    private fun hasLocationPermission(context: Context): Boolean {

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun calculateSignalQuality(rssi: Int): Int {

        return when {
            rssi >= -50 -> 100
            rssi >= -60 -> 80
            rssi >= -70 -> 60
            rssi >= -80 -> 40
            else -> 20
        }
    }
}