package com.chandan.wifianalyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chandan.wifianalyzer.navigation.Screen
import com.chandan.wifianalyzer.screens.bandwidth.BandwidthScreen
import com.chandan.wifianalyzer.screens.history.HistoryScreen
import com.chandan.wifianalyzer.screens.home.HomeScreen
import com.chandan.wifianalyzer.screens.signal.SignalScreen
import com.chandan.wifianalyzer.ui.theme.WiFiSignalStrengthAnalyzerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            WiFiSignalStrengthAnalyzerTheme {

                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable(Screen.Home.route) {

                            HomeScreen(

                                onSignalClick = {
                                    navController.navigate(Screen.Signal.route)
                                },

                                onBandwidthClick = {
                                    navController.navigate(Screen.Bandwidth.route)
                                },

                                onHistoryClick = {
                                    navController.navigate(Screen.History.route)
                                }

                            )

                        }

                        composable(Screen.Signal.route) {
                            SignalScreen()
                        }

                        composable(Screen.Bandwidth.route) {
                            BandwidthScreen()
                        }

                        composable(Screen.History.route) {
                            HistoryScreen()
                        }

                    }

                }

            }

        }

    }

}