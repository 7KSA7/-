package com.example.security.testing

import android.content.Context
import com.example.data.database.VipDatabase
import com.example.data.database.entities.SecurityLogEntity
import com.example.security.models.FileScanResult
import com.example.security.models.SecuritySeverity
import com.example.security.quarantine.QuarantineManager
import com.example.util.NotificationHelper
import java.io.File

object SimulatedThreatEnvironment {

    suspend fun generateEicarTestFile(context: Context): FileScanResult {
        val testDir = File(context.filesDir, "security_test_chamber").apply { if (!exists()) mkdirs() }
        val testFile = File(testDir, "eicar_safe_test_sample.apk")
        
        // EICAR standard harmless test string
        val eicarString = "X5O!P%@AP[4\\PZX54(P^)7CC)7}\$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!\$H+H*"
        testFile.writeText(eicarString)

        val result = FileScanResult(
            filePath = testFile.absolutePath,
            fileName = testFile.name,
            fileSize = testFile.length(),
            mimeType = "application/vnd.android.package-archive",
            sha256 = "69630e4574ec6798239b091cdd9536d6105b5e0848f213c47b640f1025a9e23a",
            threatScore = 95,
            severity = SecuritySeverity.CRITICAL,
            threatName = "SIMULATED THREAT: EICAR Standard Antivirus Test Payload",
            reasons = listOf(
                "Simulated harmless malware signature detected for safety testing",
                "Heuristic rule matched: Standard EICAR Antivirus Test Pattern",
                "Quarantine trigger verification"
            ),
            fileCategory = "APK"
        )

        // Log security incident
        val db = VipDatabase.getInstance(context)
        db.securityLogDao().insertLog(
            SecurityLogEntity(
                title = "Simulated Threat Generated",
                details = "Generated safe EICAR test file in isolated chamber (${testFile.name})",
                severity = SecuritySeverity.CRITICAL.name,
                actionTaken = "ISOLATED_AND_LOGGED",
                category = "MALWARE",
                targetItem = testFile.name
            )
        )

        NotificationHelper.showThreatAlert(
            context = context,
            title = "Simulated Threat Triggered",
            message = "Safe EICAR test payload detected. Threat Score: 95/100 (CRITICAL).",
            isCritical = true
        )

        return result
    }

    suspend fun generateSuspiciousPythonScript(context: Context): FileScanResult {
        val testDir = File(context.filesDir, "security_test_chamber").apply { if (!exists()) mkdirs() }
        val testFile = File(testDir, "test_payload_stealer.py")

        val pythonCode = """
            # Safe simulated test python script
            import subprocess
            import socket
            import base64
            
            print("Simulated credential harvester test script")
            payload = base64.b64decode("aW1wb3J0IG9z")
            subprocess.Popen(["echo", "testing_shell_execution"])
            webhook = "https://discord.com/api/webhooks/123456789/test_token_harvest"
        """.trimIndent()

        testFile.writeText(pythonCode)

        val result = FileScanResult(
            filePath = testFile.absolutePath,
            fileName = testFile.name,
            fileSize = testFile.length(),
            mimeType = "text/x-python",
            sha256 = "d41d8cd98f00b204e9800998ecf8427e",
            threatScore = 85,
            severity = SecuritySeverity.DANGEROUS,
            threatName = "SIMULATED THREAT: Python Credential Harvester",
            reasons = listOf(
                "Suspicious imports: subprocess, socket, base64",
                "Discord Webhook exfiltration URL detected",
                "Base64 obfuscated payload decoding function"
            ),
            fileCategory = "PYTHON"
        )

        val db = VipDatabase.getInstance(context)
        db.securityLogDao().insertLog(
            SecurityLogEntity(
                title = "Simulated Python Threat Generated",
                details = "Generated Python test script with dangerous import patterns (${testFile.name})",
                severity = SecuritySeverity.DANGEROUS.name,
                actionTaken = "FLAGGED_FOR_QUARANTINE",
                category = "PYTHON",
                targetItem = testFile.name
            )
        )

        NotificationHelper.showThreatAlert(
            context = context,
            title = "Python Threat Detected",
            message = "Simulated python stealer payload detected: ${testFile.name}",
            isCritical = false
        )

        return result
    }
}
