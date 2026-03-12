package com.musicstudio.pro.features.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.musicstudio.pro.features.auth.login.LoginScreen
import com.musicstudio.pro.features.auth.login.LoginViewModel
import com.musicstudio.pro.features.home.feed.FeedScreen
import com.musicstudio.pro.features.home.trending.TrendingScreen
import com.musicstudio.pro.features.messaging.chat.ChatScreen
import com.musicstudio.pro.features.profile.view_profile.ProfileScreen
import com.musicstudio.pro.features.studio.StudioScreen
import com.musicstudio.pro.features.upload.UploadScreen

sealed class BottomNavItem(val route: String, val label: String, val icon: @Composable () -> Unit) {
    object Feed : BottomNavItem("feed", "Feed", { Icon(Icons.Default.Home, contentDescription = null) })
    object Studio : BottomNavItem("studio", "Studio", { Icon(Icons.Default.Mic, contentDescription = null) })
    object Upload : BottomNavItem("upload", "Upload", { Icon(Icons.Default.Upload, contentDescription = null) })
    object Chat : BottomNavItem("chat", "Chat", { Icon(Icons.Default.Chat, contentDescription = null) })
    object Profile : BottomNavItem("profile", "Profile", { Icon(Icons.Default.Person, contentDescription = null) })
}

@Composable
fun MainBottomNav(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val items = listOf(BottomNavItem.Feed, BottomNavItem.Studio, BottomNavItem.Upload, BottomNavItem.Chat, BottomNavItem.Profile)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    androidx.compose.material.Scaffold(
        bottomBar = {
            BottomNavigation {
                items.forEach { item ->
                    BottomNavigationItem(
                        icon = item.icon,
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = BottomNavItem.Feed.route, modifier = Modifier.padding(innerPadding)) {
            composable(BottomNavItem.Feed.route) {
                FeedScreen()
            }
            composable(BottomNavItem.Studio.route) {
                StudioScreen()
            }
            composable(BottomNavItem.Upload.route) {
                UploadScreen()
            }
            composable(BottomNavItem.Chat.route) {
                ChatScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
