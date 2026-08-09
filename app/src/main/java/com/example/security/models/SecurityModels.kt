package com.example.security.models

enum class SecuritySeverity(val code: String, val level: Int) {
    SAFE("SAFE", 0),
    SUSPICIOUS("SUSPICIOUS", 1),
    DANGEROUS("DANGEROUS", 2),
    CRITICAL("CRITICAL", 3);

    companion object {
        fun fromScore(score: Int): SecuritySeverity {
            return when {
                score <= 20 -> SAFE
                score <= 50 -> SUSPICIOUS
                score <= 75 -> DANGEROUS
                else -> CRITICAL
            }
        }
    }
}

data class FileScanResult(
    val filePath: String,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val sha256: String,
    val threatScore: Int, // 0 - 100
    val severity: SecuritySeverity,
    val threatName: String,
    val reasons: List<String>,
    val fileCategory: String // APK, PYTHON, DOC, ARCHIVE, EXECUTABLE, SCRIPT, OTHER
)

data class AppScanResult(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val iconDrawable: Any? = null,
    val appSize: Long,
    val isSystemApp: Boolean,
    val securityScore: Int, // 0 - 100 (higher is safer)
    val privacyRiskScore: Int, // 0 - 100 (higher is higher risk)
    val severity: SecuritySeverity,
    val permissions: List<String>,
    val dangerousPermissions: List<String>,
    val indicators: List<String>
)

data class PythonScanResult(
    val filePath: String,
    val fileName: String,
    val suspiciousImports: List<String>,
    val dangerousCalls: List<String>,
    val netConnections: List<String>,
    val persistenceMechanisms: List<String>,
    val obfuscationFlags: List<String>,
    val threatScore: Int,
    val severity: SecuritySeverity,
    val summary: String
)

data class PrivacyRiskInfo(
    val packageName: String,
    val appName: String,
    val riskLevel: String, // Safe, Low, Medium, High, Critical
    val privacyScore: Int,
    val cameraAccess: Boolean,
    val micAccess: Boolean,
    val locationAccess: Boolean,
    val contactsAccess: Boolean,
    val smsAccess: Boolean,
    val storageAccess: Boolean,
    val accessibilityAccess: Boolean,
    val backgroundAccess: Boolean,
    val detailedSummary: String
)

data class NetworkConnectionInfo(
    val id: String,
    val packageName: String,
    val appName: String,
    val destinationHost: String,
    val destinationIp: String,
    val isBlocked: Boolean,
    val threatCategory: String?, // Malware, Phishing, Tracker, Suspicious
    val timestamp: Long = System.currentTimeMillis()
)

data class IntrusionIndicator(
    val id: String,
    val title: String,
    val description: String,
    val severity: SecuritySeverity,
    val sourceApp: String?,
    val timestamp: Long = System.currentTimeMillis()
)
