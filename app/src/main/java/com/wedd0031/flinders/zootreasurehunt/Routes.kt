package com.wedd0031.flinders.zootreasurehunt

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination

@Serializable
object AboutDestination

sealed class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Any,
) {

    data object Home : BottomNavItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = HomeDestination
    )

    data object About : BottomNavItem(
        label = "About",
        icon = Icons.Default.Info,
        route = AboutDestination
    )
}
