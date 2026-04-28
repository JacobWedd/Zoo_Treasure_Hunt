package com.wedd0031.flinders.zootreasurehunt

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wedd0031.flinders.zootreasurehunt.navigation.AboutDestination
import com.wedd0031.flinders.zootreasurehunt.navigation.BottomNavItem
import com.wedd0031.flinders.zootreasurehunt.navigation.HomeDestination
import com.wedd0031.flinders.zootreasurehunt.navigation.SettingsDestination
import com.wedd0031.flinders.zootreasurehunt.navigation.StatisticsDestination
import com.wedd0031.flinders.zootreasurehunt.ui.components.EditSightingDialog
import com.wedd0031.flinders.zootreasurehunt.ui.screens.AboutScreen
import com.wedd0031.flinders.zootreasurehunt.ui.screens.ListScreen
import com.wedd0031.flinders.zootreasurehunt.ui.screens.SettingsScreen
import com.wedd0031.flinders.zootreasurehunt.ui.screens.StatisticsScreen
import com.wedd0031.flinders.zootreasurehunt.ui.theme.ZooTreasureHuntTheme
import com.wedd0031.flinders.zootreasurehunt.viewmodel.ZooViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZooApp()
        }
    }
}

@Composable
fun ZooApp() {
    val navController = rememberNavController()
    val viewModel: ZooViewModel = viewModel()

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    val currentLang = Locale.getDefault().language
    val savedLang = prefs.getString("lang", null)

    if (savedLang != null && savedLang != currentLang) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = {
                    prefs.edit().putString("lang", currentLang).apply()
                    (context as? android.app.Activity)?.finish()
                }) {
                    Text(stringResource(R.string.restart_btn))
                }
            },
            title = { Text(stringResource(R.string.language_change_label)) },
            text = { Text(stringResource(R.string.reopen_app_label)) }
        )
    } else {
        prefs.edit().putString("lang", currentLang).apply()
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )

    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState(initial = false)



    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val drawerItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Statistics,
        BottomNavItem.Settings,
        BottomNavItem.About
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    ZooTreasureHuntTheme(darkTheme = isDarkMode) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    drawerItems.forEach { item ->
                        val isSelected = currentDestination?.hasRoute(item.route::class) == true
                        NavigationDrawerItem(
                            label = { Text(stringResource(item.label)) },
                            selected = isSelected,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = stringResource(item.label)
                                )
                            }
                        )
                    }
                }
            }
        ) {
            Scaffold { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = HomeDestination,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable<HomeDestination> {
                        ListScreen(
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                            sightings = uiState.sightings,
                            onEditClick = { animal ->
                                viewModel.selectSightingForEdit(animal)
                            },
                            onDelete = { animal ->
                                viewModel.deleteSighting(animal)
                            }
                        )
                    }
                    composable<StatisticsDestination> {
                        StatisticsScreen(
                            sightings = uiState.sightings,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }
                    composable<SettingsDestination> {
                        SettingsScreen(
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                            isSortByName = uiState.isSortByName,
                            onSortChange = { viewModel.toggleSortOrder(it) },
                            isDarkMode = isDarkMode,
                            onDarkModeChange = { viewModel.toggleDarkMode(it) }
                        )
                    }
                    composable<AboutDestination> {
                        AboutScreen(
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
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