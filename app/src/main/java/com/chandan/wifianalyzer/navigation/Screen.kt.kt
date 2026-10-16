package com.chandan.wifianalyzer.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Signal : Screen("signal")
    object Bandwidth : Screen("bandwidth")
    object History : Screen("history")
    object Settings : Screen("settings")
}