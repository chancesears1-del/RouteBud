package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.MapScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SavedRoutesScreen
import com.example.ui.theme.AppSkin
import com.example.ui.theme.RouteBuddyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteBuddyApp(
    viewModel: RouteBuddyViewModel
) {
    val activeSkin by viewModel.activeSkin.collectAsStateWithLifecycle()
    val startLocation by viewModel.startLocation.collectAsStateWithLifecycle()
    val endLocation by viewModel.endLocation.collectAsStateWithLifecycle()
    val travelMode by viewModel.travelMode.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val savedRoutes by viewModel.savedRoutes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var skinMenuExpanded by remember { mutableStateOf(false) }

    RouteBuddyTheme(activeSkin = activeSkin) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "RouteBuddy",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    },
                    actions = {
                        // Search Action -> jump to Routes tab search
                        IconButton(
                            onClick = { selectedTabIndex = 1 }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // User Initials Avatar Badge
                        val initials = remember(currentUser) {
                            currentUser?.take(2)?.uppercase() ?: "US"
                        }
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 4.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = initials,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Skin Selector Dropdown
                        Box {
                            TextButton(
                                onClick = { skinMenuExpanded = true },
                                modifier = Modifier.testTag("skin_selector_button")
                            ) {
                                Text(
                                    text = "${activeSkin.iconEmoji} ${activeSkin.displayName.replace("Skin: ", "")}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Skin"
                                )
                            }

                            DropdownMenu(
                                expanded = skinMenuExpanded,
                                onDismissRequest = { skinMenuExpanded = false }
                            ) {
                                AppSkin.values().forEach { skin ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(skin.iconEmoji)
                                                Text(skin.displayName)
                                            }
                                        },
                                        onClick = {
                                            viewModel.setSkin(skin)
                                            skinMenuExpanded = false
                                        },
                                        modifier = Modifier.testTag("skin_option_${skin.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                        label = { Text("Map") },
                        modifier = Modifier.testTag("nav_tab_map")
                    )
                    NavigationBarItem(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        icon = { Icon(Icons.AutoMirrored.Default.AltRoute, contentDescription = "Routes") },
                        label = { Text("Routes") },
                        modifier = Modifier.testTag("nav_tab_routes")
                    )
                    NavigationBarItem(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Crossfade(
                    targetState = selectedTabIndex,
                    label = "tabCrossfade"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> MapScreen(
                            startLocation = startLocation,
                            endLocation = endLocation,
                            travelMode = travelMode,
                            chatMessages = chatMessages,
                            activeSkin = activeSkin,
                            onStartLocationChange = { viewModel.setStartLocation(it) },
                            onEndLocationChange = { viewModel.setEndLocation(it) },
                            onTravelModeChange = { viewModel.setTravelMode(it) },
                            onCalculateClick = { viewModel.calculateAndSaveRoute() },
                            onPresetClick = { start, end, mode ->
                                viewModel.loadPresetRoute(start, end, mode)
                            }
                        )
                        1 -> SavedRoutesScreen(
                            routes = savedRoutes,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onDeleteRoute = { viewModel.deleteRoute(it) },
                            onClearAll = { viewModel.clearAllRoutes() },
                            onLoadOnMap = { start, end, mode ->
                                viewModel.setStartLocation(start)
                                viewModel.setEndLocation(end)
                                viewModel.setTravelMode(mode)
                                selectedTabIndex = 0
                            }
                        )
                        2 -> ProfileScreen(
                            currentUser = currentUser,
                            isLoggedIn = isLoggedIn,
                            totalSavedRoutesCount = savedRoutes.size,
                            onLoginClick = { viewModel.login(it) },
                            onLogoutClick = { viewModel.logout() }
                        )
                    }
                }
            }
        }
    }
}
