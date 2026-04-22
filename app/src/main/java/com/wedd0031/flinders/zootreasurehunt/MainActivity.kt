package com.wedd0031.flinders.zootreasurehunt

import android.Manifest
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.wedd0031.flinders.zootreasurehunt.ui.theme.ZooTreasureHuntTheme
import com.wedd0031.flinders.zootreasurehunt.viewmodel.ZooViewModel
import android.net.Uri
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wedd0031.flinders.zootreasurehunt.navigation.AboutDestination
import com.wedd0031.flinders.zootreasurehunt.navigation.BottomNavItem
import com.wedd0031.flinders.zootreasurehunt.navigation.HomeDestination
import com.wedd0031.flinders.zootreasurehunt.navigation.SettingsDestination
import com.wedd0031.flinders.zootreasurehunt.ui.screens.SettingsScreen
import com.wedd0031.flinders.zootreasurehunt.utils.FileUtils
import com.wedd0031.flinders.zootreasurehunt.ui.components.EditSightingDialog
import com.wedd0031.flinders.zootreasurehunt.ui.screens.ListScreen
import com.wedd0031.flinders.zootreasurehunt.ui.screens.AboutScreen
import com.wedd0031.flinders.zootreasurehunt.data.FileSightingRepository
import com.wedd0031.flinders.zootreasurehunt.data.SettingsRepository
import com.wedd0031.flinders.zootreasurehunt.data.SightingRepository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                ZooApp()
            }
        }
    }
}

@Composable
fun ZooApp() {
    val navController = rememberNavController()
    val viewModel: ZooViewModel = viewModel()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )

    val uiState by viewModel.uiState.collectAsState()



    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val bottomItems = listOf(BottomNavItem.Home, BottomNavItem.Settings, BottomNavItem.About)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->
                    val isSelected = currentDestination?.hasRoute(item.route::class) == true
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HomeDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<HomeDestination> {
                    ListScreen(
                        sightings = uiState.sightings,
                        onEditClick = { animal ->
                            viewModel.selectSightingForEdit(animal)
                        },
                        onDelete = { animal ->
                            viewModel.deleteSighting(animal)
                        }
                    )
                }
                composable<SettingsDestination> {
                    SettingsScreen(
                        isSortByName = uiState.isSortByName,
                        onSortChange = { viewModel.toggleSortOrder(it) }
                    )
                }
                composable<AboutDestination> {
                    AboutScreen()
                }
            }
            if (uiState.isDialogVisible) {
                uiState.selectedSighting?.let { sighting ->
                    EditSightingDialog(
                        sighting = sighting,
                        onDismiss = { viewModel.dismissDialog() },
                        onSave = { updated ->
                            viewModel.updateSighting(updated)
                            viewModel.dismissDialog()
                        }
                    )
                    }
            }
        }
    }



/*
@Preview(showBackground = true)
@Composable
fun ZooAppPreview() {
    ZooTreasureHuntTheme {
        ZooApp(
            sightingRepository = TODO(),
            settingsRepository = TODO()
        )
    }
} */