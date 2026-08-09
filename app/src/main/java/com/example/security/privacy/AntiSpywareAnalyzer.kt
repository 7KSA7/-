package com.example.security.privacy

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.security.models.PrivacyRiskInfo

object AntiSpywareAnalyzer {

    fun analyzeAppPrivacy(context: Context, packageName: String): PrivacyRiskInfo {
        val pm = context.packageManager
        return try {
            val info = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val appName = info.applicationInfo?.loadLabel(pm)?.toString() ?: packageName
            val permissions = info.requestedPermissions ?: emptyArray()

            var hasCamera = false
            var hasMic = false
            var hasLocation = false
            var hasContacts = false
            var hasSms = false
            var hasStorage = false
            var hasAccessibility = false
            var hasBackground = false

            var score = 0

            for (p in permissions) {
                when {
                    p.contains("CAMERA") -> { hasCamera = true; score += 20 }
                    p.contains("RECORD_AUDIO") -> { hasMic = true; score += 20 }
                    p.contains("LOCATION") -> { hasLocation = true; score += 15 }
                    p.contains("READ_CONTACTS") -> { hasContacts = true; score += 15 }
                    p.contains("READ_SMS") || p.contains("RECEIVE_SMS") -> { hasSms = true; score += 20 }
                    p.contains("READ_EXTERNAL_STORAGE") || p.contains("WRITE_EXTERNAL_STORAGE") -> { hasStorage = true; score += 10 }
                    p.contains("ACCESSIBILITY") -> { hasAccessibility = true; score += 30 }
                    p.contains("RECEIVE_BOOT_COMPLETED") || p.contains("FOREGROUND_SERVICE") -> { hasBackground = true; score += 10 }
                }
            }

            val finalScore = score.coerceAtMost(100)
            val riskLevel = when {
                finalScore <= 15 -> "Safe 🟢"
                finalScore <= 35 -> "Low Risk 🟡"
                finalScore <= 60 -> "Medium Risk 🟠"
                finalScore <= 80 -> "High Risk 🔴"
                else -> "Critical Privacy Risk ☠️"
            }

            val summaryBuilder = StringBuilder()
            summaryBuilder.append("Privacy Risk Score: $finalScore/100 ($riskLevel).\n")
            if (hasAccessibility) summaryBuilder.append("• Holds Accessibility permission (Can observe screen and simulate touches).\n")
            if (hasCamera) summaryBuilder.append("• Has access to Camera sensor.\n")
            if (hasMic) summaryBuilder.append("• Has access to Microphone audio recording.\n")
            if (hasLocation) summaryBuilder.append("• Tracks device precise/background location.\n")
            if (hasSms) summaryBuilder.append("• Reads SMS messages and incoming OTPs.\n")
            if (hasContacts) summaryBuilder.append("• Reads address book contacts.\n")
            if (summaryBuilder.length < 50) {
                summaryBuilder.append("• Standard essential application permissions requested.")
            }

            PrivacyRiskInfo(
                packageName = packageName,
                appName = appName,
                riskLevel = riskLevel,
                privacyScore = finalScore,
                cameraAccess = hasCamera,
                micAccess = hasMic,
                locationAccess = hasLocation,
                contactsAccess = hasContacts,
                smsAccess = hasSms,
                storageAccess = hasStorage,
                accessibilityAccess = hasAccessibility,
                backgroundAccess = hasBackground,
                detailedSummary = summaryBuilder.toString()
            )
        } catch (e: Exception) {
            PrivacyRiskInfo(
                packageName = packageName,
                appName = packageName,
                riskLevel = "Unknown",
                privacyScore = 0,
                cameraAccess = false,
                micAccess = false,
                locationAccess = false,
                contactsAccess = false,
                smsAccess = false,
                storageAccess = false,
                accessibilityAccess = false,
                backgroundAccess = false,
                detailedSummary = "Could not evaluate privacy info"
            )
        }
    }

    fun scanAllAppsPrivacy(context: Context): List<PrivacyRiskInfo> {
        val pm = context.packageManager
        val installed = pm.getInstalledPackages(0)
        return installed
            .filter { ((it.applicationInfo?.flags ?: 0) and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .map { analyzeAppPrivacy(context, it.packageName) }
            .sortedByDescending { it.privacyScore }
    }
}
