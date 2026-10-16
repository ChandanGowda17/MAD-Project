package com.chandan.wifianalyzer.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onSignalClick: () -> Unit,
    onBandwidthClick: () -> Unit,
    onHistoryClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Wi-Fi Signal Analyzer",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Analyze Wi-Fi signal strength and monitor bandwidth."
        )

        FeatureCard(
            title = "Signal Strength Detector",
            description = "Find the strongest Wi-Fi location.",
            icon = {
                Icon(
                    Icons.Default.NetworkWifi,
                    contentDescription = null
                )
            },
            onClick = onSignalClick
        )

        FeatureCard(
            title = "Bandwidth Detector",
            description = "Monitor upload and download speed.",
            icon = {
                Icon(
                    Icons.Default.Speed,
                    contentDescription = null
                )
            },
            onClick = onBandwidthClick
        )

        FeatureCard(
            title = "History",
            description = "View previous scans.",
            icon = {
                Icon(
                    Icons.Default.History,
                    contentDescription = null
                )
            },
            onClick = onHistoryClick
        )

    }

}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            icon()

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(description)

            }

            Button(onClick = onClick) {
                Text("Open")
            }

        }

    }

}