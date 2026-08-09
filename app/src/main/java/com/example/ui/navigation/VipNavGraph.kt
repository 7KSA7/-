package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ui.screens.about.AboutDevelopersScreen
import com.example.ui.screens.ai.AiAssistantScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.extremedefense.ExtremeDefenseScreen
import com.example.ui.screens.lockdown.EmergencyLockdownScreen
import com.example.ui.screens.logs.SecurityLogsScreen
import com.example.ui.screens.network.NetworkProtectionScreen
import com.example.ui.screens.privacy.PrivacyShieldScreen
import com.example.ui.screens.python.PythonScannerScreen
import com.example.ui.screens.quarantine.QuarantineScreen
import com.example.ui.screens.scan.FullScanScreen
import com.example.ui.screens.securitycenter.SecurityCenterScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.vault.SecureVaultScreen
import com.example.ui.screens.website.WebsiteScannerScreen

@Composable
fun VipNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.FullScan.route) { FullScanScreen(navController) }
        composable(Screen.PythonScan.route) { PythonScannerScreen(navController) }
        composable(Screen.PrivacyShield.route) { PrivacyShieldScreen(navController) }
        composable(Screen.NetworkProtection.route) { NetworkProtectionScreen(navController) }
        composable(Screen.SecureVault.route) { SecureVaultScreen(navController) }
        composable(Screen.Quarantine.route) { QuarantineScreen(navController) }
        composable(Screen.AiAssistant.route) { AiAssistantScreen(navController) }
        composable(Screen.SecurityLogs.route) { SecurityLogsScreen(navController) }
        composable(Screen.EmergencyLockdown.route) { EmergencyLockdownScreen(navController) }
        composable(Screen.AboutDevelopers.route) { AboutDevelopersScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.WebsiteScanner.route) { WebsiteScannerScreen(navController) }
        composable(Screen.ExtremeDefense.route) { ExtremeDefenseScreen(navController) }
        composable(Screen.SecurityCenter.route) { SecurityCenterScreen(navController) }
    }
}
