package com.aiflow.workflow.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aiflow.workflow.ui.profile.ProfileScreen
import com.aiflow.workflow.ui.task.TaskCenterScreen
import com.aiflow.workflow.ui.workflow.WorkflowListScreen

sealed class HomeTab(val route: String, val title: String, val icon: ImageVector) {
    object Workflows : HomeTab("workflows", "工作流", Icons.Default.Home)
    object Tasks : HomeTab("tasks", "任务中心", Icons.Default.TaskAlt)
    object Profile : HomeTab("profile", "个人中心", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onTaskCenterClick: () -> Unit,
    onProfileClick: () -> Unit,
    onWorkflowClick: (String) -> Unit,
    viewModel: com.aiflow.workflow.ui.workflow.WorkflowViewModel = hiltViewModel()
) {
    val homeNavController = rememberNavController()
    val currentBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.Workflows.route) }
    
    // 同步当前路由与选中标签
    if (currentRoute != null && currentRoute != selectedTab) {
        selectedTab = currentRoute
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                listOf(HomeTab.Workflows, HomeTab.Tasks, HomeTab.Profile).forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab.route,
                        onClick = {
                            if (selectedTab != tab.route) {
                                homeNavController.navigate(tab.route) {
                                    // 避免同一目标重复入栈
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                selectedTab = tab.route
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HomeNavHost(
                navController = homeNavController,
                modifier = Modifier.fillMaxSize(),
                onWorkflowClick = onWorkflowClick,
                onTaskCenterClick = onTaskCenterClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

@Composable
private fun HomeNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onWorkflowClick: (String) -> Unit,
    onTaskCenterClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = HomeTab.Workflows.route,
        modifier = modifier
    ) {
        composable(HomeTab.Workflows.route) {
            WorkflowListScreen(
                onWorkflowClick = onWorkflowClick
            )
        }
        
        composable(HomeTab.Tasks.route) {
            TaskCenterScreen(
                onBackClick = { /* 在底部导航中，点击其他标签即可切换 */ },
                onTaskClick = { task ->
                    // 可以跳转到任务详情页
                }
            )
        }
        
        composable(HomeTab.Profile.route) {
            ProfileScreen(
                onBackClick = { /* 同任务中心 */ },
                onLogout = onProfileClick
            )
        }
    }
}