
package com.chandan.wifianalyzer.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

data class SpeedTestResult(
    val downloadMbps: Double,
    val uploadMbps: Double,
    val pingMs: Long
)

object SpeedTestUtils {

    private const val DOWNLOAD_URL =
        "https://speed.cloudflare.com/__down?bytes=5000000"

    private const val UPLOAD_URL =
        "https://speed.cloudflare.com/__up"

    private const val PING_URL =
        "https://speed.cloudflare.com/__down?bytes=0"

    suspend fun testPing(): Long = withContext(Dispatchers.IO) {
        val start = System.nanoTime()

        val connection = URL(PING_URL)
            .openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.connect()

            connection.inputStream.use { it.readBytes() }

            (System.nanoTime() - start) / 1_000_000
        } finally {
            connection.disconnect()
        }
    }

    suspend fun testDownload(): Double = withContext(Dispatchers.IO) {
        val connection = URL(DOWNLOAD_URL)
            .openConnection() as HttpURLConnection

        try {
            connection.connectTimeout = 15000
            connection.readTimeout = 30000
            connection.connect()

            val start = System.nanoTime()
            var totalBytes = 0L
            val buffer = ByteArray(8192)

            connection.inputStream.use { input ->
                while (true) {
                    val count = input.read(buffer)
                    if (count == -1) break
                    totalBytes += count
                }
            }

            val elapsedSeconds =
                (System.nanoTime() - start) / 1_000_000_000.0

            if (elapsedSeconds <= 0.0) 0.0
            else totalBytes * 8.0 / elapsedSeconds / 1_000_000.0
        } finally {
            connection.disconnect()
        }
    }

    suspend fun testUpload(): Double = withContext(Dispatchers.IO) {
        val payload = ByteArray(1_000_000)

        val connection = URL(UPLOAD_URL)
            .openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.connectTimeout = 15000
            connection.readTimeout = 30000
            connection.setFixedLengthStreamingMode(payload.size)

            val start = System.nanoTime()

            connection.outputStream.use { output ->
                output.write(payload)
            }

            val responseCode = connection.responseCode

            if (responseCode !in 200..299) {
                throw IllegalStateException(
                    "Upload failed: HTTP $responseCode"
                )
            }

            val elapsedSeconds =
                (System.nanoTime() - start) / 1_000_000_000.0

            if (elapsedSeconds <= 0.0) 0.0
            else payload.size * 8.0 /
                    elapsedSeconds / 1_000_000.0
        } finally {
            connection.disconnect()
        }
    }

    suspend fun runTest(): SpeedTestResult {
        val ping = testPing()
        val download = testDownload()
        val upload = testUpload()

        return SpeedTestResult(
            downloadMbps = download,
            uploadMbps = upload,
            pingMs = ping
        )
    }
}
