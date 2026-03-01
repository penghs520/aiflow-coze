package com.aiflow.workflow.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aiflow.workflow.ui.auth.LoginScreen
import com.aiflow.workflow.ui.auth.RegisterScreen
import com.aiflow.workflow.ui.auth.ForgotPasswordScreen
import com.aiflow.workflow.ui.home.HomeScreen
import com.aiflow.workflow.ui.task.TaskCenterScreen
import com.aiflow.workflow.ui.profile.ProfileScreen
import com.aiflow.workflow.ui.workflow.WorkflowListScreen
import com.aiflow.workflow.ui.workflow.WorkflowDetailScreen
import com.aiflow.workflow.ui.workflow.ParameterInputScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object TaskCenter : Screen("task_center")
    object Profile : Screen("profile")
    object WorkflowList : Screen("workflows")
    object WorkflowDetail : Screen("workflows/{id}") {
        fun createRoute(id: String) = "workflows/$id"
    }
    object ParameterInput : Screen("workflows/{id}/input") {
        fun createRoute(id: String) = "workflows/$id/input"
    }
}

@Composable
fun WorkflowApp() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.Home.route) },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onResetSuccess = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onTaskCenterClick = { navController.navigate(Screen.TaskCenter.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onWorkflowClick = { navController.navigate(Screen.WorkflowList.route) }
            )
        }
        composable(Screen.TaskCenter.route) {
            TaskCenterScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogout = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.WorkflowList.route) {
            WorkflowListScreen(
                onWorkflowClick = { id ->
                    navController.navigate(Screen.WorkflowDetail.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.WorkflowDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val workflowId = backStackEntry.arguments?.getString("id") ?: ""
            WorkflowDetailScreen(
                workflowId = workflowId,
                onBackClick = { navController.popBackStack() },
                onStartClick = { id ->
                    navController.navigate(Screen.ParameterInput.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.ParameterInput.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val workflowId = backStackEntry.arguments?.getString("id") ?: ""
            ParameterInputScreen(
                workflowId = workflowId,
                onBackClick = { navController.popBackStack() },
                onSubmitSuccess = { taskId ->
                    // 跳转到任务详情或任务中心
                    navController.navigate(Screen.TaskCenter.route)
                }
            )
        }
    }
}