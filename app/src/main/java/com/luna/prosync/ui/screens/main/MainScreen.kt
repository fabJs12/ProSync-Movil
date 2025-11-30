package com.luna.prosync.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.luna.prosync.navigation.Routes
import com.luna.prosync.ui.screens.create_project.CreateProjectScreen
import com.luna.prosync.ui.screens.home.HomeScreen
import com.luna.prosync.ui.screens.my_tasks.MyTasksScreen
import com.luna.prosync.ui.screens.projects.ProjectScreen
import com.luna.prosync.ui.theme.DarkBlue

private sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarScreen("home", "Inicio", Icons.Outlined.Home)
    object Projects : BottomBarScreen("projects", "Tableros", Icons.Outlined.Dashboard)
    object MyTasks : BottomBarScreen("my_tasks", "Mis Tareas", Icons.Outlined.CheckCircle)
}

private val bottomNavItems = listOf(
    BottomBarScreen.Home,
    BottomBarScreen.Projects,
    BottomBarScreen.MyTasks
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    rootNavController: NavController,
    onLogout: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == BottomBarScreen.Projects.route) {
                ExtendedFloatingActionButton(
                    onClick = {
                        rootNavController.navigate(Routes.CREATE_PROJECT)
                    },
                    containerColor = DarkBlue,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nuevo Proyecto") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavGraph(
                bottomNavController = bottomNavController,
                rootNavController = rootNavController,
                onLogout = onLogout
            )
        }
    }
}

@Composable
fun BottomNavGraph(
    bottomNavController: NavHostController,
    rootNavController: NavController,
    onLogout: () -> Unit
) {
    NavHost(
        navController = bottomNavController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(BottomBarScreen.Home.route) {
            HomeScreen(
                onLogout = onLogout,
                onNavigateToNotifications = {
                    rootNavController.navigate(Routes.NOTIFICATIONS)
                }
            )
        }

        composable(BottomBarScreen.Projects.route) {
            ProjectScreen(
                onProjectClick = { projectId ->
                    rootNavController.navigate(Routes.projectDetail(projectId))
                }
            )
        }

        composable(BottomBarScreen.MyTasks.route) {
            MyTasksScreen(
                onNavigateBack = { },
                onTaskClick = { taskId, projectId ->
                    rootNavController.navigate(Routes.taskDetail(projectId, taskId))
                }
            )
        }
    }
}