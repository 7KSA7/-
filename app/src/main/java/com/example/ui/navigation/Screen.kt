package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object FullScan : Screen("full_scan")
    object MalwareScan : Screen("malware_scan")
    object PythonScan : Screen("python_scan")
    object PrivacyShield : Screen("privacy_shield")
    object NetworkProtection : Screen("network_protection")
    object SecureVault : Screen("secure_vault")
    object Quarantine : Screen("quarantine")
    object IntrusionDetection : Screen("intrusion_detection")
    object AiAssistant : Screen("ai_assistant")
    object SecurityLogs : Screen("security_logs")
    object EmergencyLockdown : Screen("emergency_lockdown")
    object AboutDevelopers : Screen("about_developers")
    object Settings : Screen("settings")
    object WebsiteScanner : Screen("website_scanner")
    object ExtremeDefense : Screen("extreme_defense")
    object SecurityCenter : Screen("security_center")
}
