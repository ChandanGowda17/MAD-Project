
package com.chandan.wifianalyzer.screens.signal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignalGraph(
    readings: Map<Int, Int?>,
    modifier: Modifier = Modifier
) {
    val available = (1..4).filter { readings[it] != null }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Signal Strength by Corner",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            "RSSI in dBm — values closer to 0 indicate stronger signal",
            style = MaterialTheme.typography.bodySmall
        )

        if (available.isEmpty()) {
            Text("Capture at least one corner to display the graph.")
            return@Column
        }

        val gridColor = MaterialTheme.colorScheme.outlineVariant
        val lineColor = MaterialTheme.colorScheme.primary
        val labelColor = MaterialTheme.colorScheme.onSurface

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            val left = 48.dp.toPx()
            val right = size.width - 16.dp.toPx()
            val top = 16.dp.toPx()
            val bottom = size.height - 30.dp.toPx()

            val plotWidth = right - left
            val plotHeight = bottom - top

            // Fixed scale makes different scans comparable.
            // -30 dBm is strong; -90 dBm is weak.
            fun yFor(rssi: Int): Float {
                val bounded = rssi.coerceIn(-90, -30)
                return top + ((-30f - bounded) / 60f) * plotHeight
            }

            for (tick in listOf(-30, -45, -60, -75, -90)) {
                val y = yFor(tick)

                drawLine(
                    color = gridColor,
                    start = Offset(left, y),
                    end = Offset(right, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val points = available.map { corner ->
                val x = left + ((corner - 1) / 3f) * plotWidth
                Offset(x, yFor(readings[corner]!!))
            }

            points.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = lineColor,
                    start = start,
                    end = end,
                    strokeWidth = 3.dp.toPx()
                )
            }

            points.forEach { point ->
                drawCircle(
                    color = lineColor,
                    radius = 5.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = point
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (1..4).forEach { corner ->
                Column {
                    Text(
                        "Corner $corner",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = labelColor
                    )
                    Text(
                        readings[corner]?.let { "$it dBm" } ?: "--",
                        fontSize = 12.sp,
                        color = labelColor
                    )
                }
            }
        }
    }
}
