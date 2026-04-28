package com.wedd0031.flinders.zootreasurehunt.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.wedd0031.flinders.zootreasurehunt.R
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination

@Serializable
object StatisticsDestination

@Serializable
object AboutDestination

@Serializable
object SettingsDestination

sealed class BottomNavItem(
    val label: Int,
    val icon: ImageVector,
    val route: Any,
) {

    data object Home : BottomNavItem(
        label = (R.string.home_btn),
        icon = Icons.Default.Home,
        route = HomeDestination
    )

    data object Statistics : BottomNavItem(
        label = R.string.statistics_btn,
        icon = Icons.Default.BarChart,
        route = StatisticsDestination
    )

    data object Settings : BottomNavItem(
        (R.string.settings_btn),
        Icons.Filled.Settings,
        SettingsDestination
    )

    data object About : BottomNavItem(
        label = (R.string.about_btn),
        icon = Icons.Default.Info,
        route = AboutDestination
    )
}
