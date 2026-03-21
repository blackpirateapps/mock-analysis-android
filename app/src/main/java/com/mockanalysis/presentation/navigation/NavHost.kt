package com.mockanalysis.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mockanalysis.presentation.analysis.AnalysisHubScreen
import com.mockanalysis.presentation.common.components.BottomNavItem
import com.mockanalysis.presentation.common.components.MockAnalysisBottomBar
import com.mockanalysis.presentation.common.components.MockAnalysisTopBar
import com.mockanalysis.presentation.profile.ProfileScreen

/**
 * Navigation routes for the app.
 */
object NavRoutes {
    const val DASHBOARD = "dashboard"
    const val INPUT = "input"
    const val HISTORY = "history"
    const val ANALYSIS = "analysis"
    const val PROFILE = "profile"
}

/**
 * Main navigation host for the app.
 */
@Composable
fun MockAnalysisNavHost(
    navController: NavHostController = rememberNavController()
) {
    var currentRoute by remember { mutableStateOf(NavRoutes.ANALYSIS) }
    
    // Sample avatar URL (would come from user profile in real app)
    val avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDoR95ZIhK435esYJt7VnhTu9fOKoYA5cjsiguq6VrY6fGAbOvmHa4yizzNV1K9_MXAK78nUSWCVMASpjV-T9NrvsZnLEprSCow8TMUkNLfBTvfHJHwnXiG-22uNlpJ-94CIhwjjRhOtvdrKV78OhPb3_3gPTavzacJ6ZoO0YeB6g46zZyGcocs-sVsZI73atTVDxvSdwaeX25RzdRx0Ii2gtYf4CRsBd0mz2MtkE4VGcSXmHqbdQIQ6Ya4VQaAc79Vvl52WwB-kpk"

    Scaffold(
        topBar = {
            MockAnalysisTopBar(
                avatarUrl = avatarUrl,
                onNotificationClick = { /* Handle notification click */ }
            )
        },
        bottomBar = {
            MockAnalysisBottomBar(
                currentRoute = currentRoute,
                onNavigate = { item ->
                    currentRoute = item.route
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = NavRoutes.ANALYSIS
            ) {
                composable(NavRoutes.DASHBOARD) {
                    // Placeholder for Dashboard screen
                    PlaceholderScreen(title = "Dashboard")
                }
                
                composable(NavRoutes.INPUT) {
                    // Placeholder for Input screen
                    PlaceholderScreen(title = "Manual Score Entry")
                }
                
                composable(NavRoutes.HISTORY) {
                    // Placeholder for History screen
                    PlaceholderScreen(title = "Mock Analysis History")
                }
                
                composable(NavRoutes.ANALYSIS) {
                    AnalysisHubScreen(
                        onStartPractice = { /* Navigate to practice */ }
                    )
                }
                
                composable(NavRoutes.PROFILE) {
                    ProfileScreen(
                        onNavigateToSecurity = { /* Navigate to security */ },
                        onNavigateToNotifications = { /* Navigate to notifications */ },
                        onNavigateToHelp = { /* Navigate to help */ },
                        onNavigateToPrivacy = { /* Navigate to privacy */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "$title\n(Coming Soon)",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = com.mockanalysis.presentation.theme.MockAnalysisColors.OnSurfaceVariant
        )
    }
}
