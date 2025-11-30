package com.luna.prosync.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.luna.prosync.ui.screens.create_project.CreateProjectScreen
import com.luna.prosync.ui.screens.create_task.CreateTaskScreen
import com.luna.prosync.ui.screens.login.LoginScreen
import com.luna.prosync.ui.screens.main.MainScreen
import com.luna.prosync.ui.screens.my_tasks.MyTasksScreen
import com.luna.prosync.ui.screens.notifications.NotificationsScreen
import com.luna.prosync.ui.screens.project_detail.ProjectDetailScreen
import com.luna.prosync.ui.screens.projects.ProjectScreen
import com.luna.prosync.ui.screens.register.RegisterScreen
import com.luna.prosync.ui.screens.task_detail.TaskDetailScreen
import com.luna.prosync.ui.screens.team.TeamScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val CREATE_PROJECT = "create_project"

    const val PROJECT_DETAIL = "project_detail/{projectId}"
    fun projectDetail(projectId: Int) = "project_detail/$projectId"

    const val CREATE_TASK = "create_task/{boardId}"
    fun createTask(boardId: Int) = "create_task/$boardId"

    const val TASK_DETAIL = "task_detail/{projectId}/{taskId}"
    fun taskDetail(projectId: Int, taskId: Int) = "task_detail/$projectId/$taskId"

    const val MY_TASKS = "my_tasks"
    const val NOTIFICATIONS = "notifications"

    const val TEAM = "team/{projectId}"
    fun team(projectId: Int) = "team/$projectId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.MAIN) {
            MainScreen (
                rootNavController = navController,
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CREATE_PROJECT) {
            CreateProjectScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.PROJECT_DETAIL,
            arguments = listOf(navArgument("projectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getInt("projectId") ?: 0

            ProjectDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onCreateTask = { boardId ->
                    navController.navigate(Routes.createTask(boardId))
                },
                onTaskClick = { taskId ->
                    navController.navigate(Routes.taskDetail(projectId, taskId))
                },
                onTeamClick = {
                    navController.navigate(Routes.team(projectId))
                }
            )
        }

        composable(
            route = Routes.CREATE_TASK,
            arguments = listOf(navArgument("boardId") { type = NavType.IntType })
        ) {
            CreateTaskScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.TASK_DETAIL,
            arguments = listOf(
                navArgument("projectId") { type = NavType.IntType },
                navArgument("taskId") { type = NavType.IntType }
            )
        ) {
            TaskDetailScreen(
                onNavigateBack = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh_project_detail", true)
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.MY_TASKS) {
            MyTasksScreen(
                onNavigateBack = { navController.popBackStack() },
                onTaskClick = { taskId, projectId ->
                    navController.navigate(Routes.taskDetail(projectId, taskId))
                }
            )
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.TEAM,
            arguments = listOf(navArgument("projectId") { type = NavType.IntType })
        ) {
            TeamScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}