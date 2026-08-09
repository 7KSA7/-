package com.example.security.scanner

import com.example.security.models.PythonScanResult
import com.example.security.models.SecuritySeverity
import java.io.File
import java.util.regex.Pattern

object PythonStaticAnalyzer {

    fun analyzeContent(fileName: String, filePath: String, code: String): PythonScanResult {
        val suspiciousImports = mutableListOf<String>()
        val dangerousCalls = mutableListOf<String>()
        val netConnections = mutableListOf<String>()
        val persistence = mutableListOf<String>()
        val obfuscation = mutableListOf<String>()

        var score = 0

        // 1. Check Imports
        val importPatterns = listOf(
            "subprocess" to "Spawns system sub-processes or shell commands",
            "socket" to "Raw network socket creation capabilities",
            "requests" to "HTTP requests (potential exfiltration)",
            "urllib" to "Network URL downloader",
            "ctypes" to "Native C memory & API calls",
            "os" to "System OS interaction",
            "shutil" to "File system manipulation",
            "pynput" to "Keylogger / input monitoring library",
            "winreg" to "Windows registry manipulation",
            "browser_cookie3" to "Steals browser cookies & credentials",
            "discord" to "Discord bot / Webhook integration"
        )

        for ((lib, desc) in importPatterns) {
            val pattern = Pattern.compile("(?:import|from)\\s+$lib", Pattern.MULTILINE)
            if (pattern.matcher(code).find()) {
                suspiciousImports.add("$lib ($desc)")
                score += 12
            }
        }

        // 2. Dangerous Calls & Shell Execution
        val callPatterns = listOf(
            "eval\\(" to "Dynamic code execution via eval()",
            "exec\\(" to "Dynamic code execution via exec()",
            "subprocess\\.Popen" to "Subprocess creation with custom args",
            "os\\.system" to "Direct shell execution via os.system()",
            "os\\.popen" to "Piped shell command execution",
            "base64\\.b64decode" to "Decodes base64 obfuscated payload",
            "codecs\\.decode" to "Decodes obfuscated strings/payloads",
            "__import__" to "Dynamic module import injection",
            "open\\(.*['\"]\\/etc\\/passwd['\"]" to "Reads sensitive system files",
            "open\\(.*['\"]\\/data\\/data" to "Attempts accessing Android private storage",
            "ctypes\\.windll" to "Direct Win32 API access",
            "http:\\/\\/|https:\\/\\/" to "Contains hardcoded URLs"
        )

        for ((regex, desc) in callPatterns) {
            val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            if (pattern.matcher(code).find()) {
                dangerousCalls.add(desc)
                score += 15
            }
        }

        // 3. Credential & Token Harvesting
        val tokenPatterns = listOf(
            "discord\\.com\\/api\\/webhooks" to "Discord Webhook data exfiltration URL",
            "token" to "Token keyword reference",
            "Chrome\\\\User Data" to "Browser profile target",
            "Telegram" to "Telegram session data harvesting",
            "cookie" to "Cookie storage extraction"
        )

        for ((regex, desc) in tokenPatterns) {
            val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            if (pattern.matcher(code).find()) {
                netConnections.add(desc)
                score += 20
            }
        }

        // 4. Obfuscation & Entropy Indicators
        if (code.contains("\\x") && code.count { it == '\\' } > 30) {
            obfuscation.add("High density of hex-escaped byte sequences")
            score += 25
        }
        if (code.length > 200 && code.split("\n").size < 3) {
            obfuscation.add("Minified or single-line obfuscated script")
            score += 20
        }

        val finalScore = score.coerceAtMost(100)
        val severity = SecuritySeverity.fromScore(finalScore)

        val summary = when (severity) {
            SecuritySeverity.SAFE -> "No suspicious patterns or dangerous calls detected."
            SecuritySeverity.SUSPICIOUS -> "Contains system interaction imports that require review."
            SecuritySeverity.DANGEROUS -> "High risk python script containing potential shell execution or net exfiltration."
            SecuritySeverity.CRITICAL -> "CRITICAL THREAT: Script contains credential harvesting or obfuscated execution payloads!"
        }

        return PythonScanResult(
            filePath = filePath,
            fileName = fileName,
            suspiciousImports = suspiciousImports,
            dangerousCalls = dangerousCalls,
            netConnections = netConnections,
            persistenceMechanisms = persistence,
            obfuscationFlags = obfuscation,
            threatScore = finalScore,
            severity = severity,
            summary = summary
        )
    }

    fun analyzeFile(file: File): PythonScanResult {
        if (!file.exists() || !file.canRead()) {
            return PythonScanResult(
                filePath = file.absolutePath,
                fileName = file.name,
                suspiciousImports = emptyList(),
                dangerousCalls = emptyList(),
                netConnections = emptyList(),
                persistenceMechanisms = emptyList(),
                obfuscationFlags = emptyList(),
                threatScore = 0,
                severity = SecuritySeverity.SAFE,
                summary = "File unreadable or empty"
            )
        }
        val content = try {
            file.readText()
        } catch (e: Exception) {
            ""
        }
        return analyzeContent(file.name, file.absolutePath, content)
    }
}
