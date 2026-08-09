package com.example.ui.screens.python

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.repository.SecurityRepository
import com.example.security.models.FileScanResult
import com.example.security.models.PythonScanResult
import com.example.security.models.SecuritySeverity
import com.example.security.scanner.PythonStaticAnalyzer
import com.example.security.testing.SimulatedThreatEnvironment
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.ThreatBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PythonScannerScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { SecurityRepository(context) }

    var inputCode by remember { mutableStateOf("") }
    var scanResult by remember { mutableStateOf<PythonScanResult?>(null) }

    fun analyzePythonCode() {
        if (inputCode.isBlank()) return
        val res = PythonStaticAnalyzer.analyzeContent("user_input_script.py", "memory://user_input_script.py", inputCode)
        scanResult = res

        scope.launch {
            repo.logEvent(
                title = "Python Static Scan Executed",
                details = res.summary,
                severity = res.severity.name,
                category = "PYTHON",
                target = res.fileName
            )
        }
    }

    fun generateSimulatedScript() {
        scope.launch {
            val fileRes = SimulatedThreatEnvironment.generateSuspiciousPythonScript(context)
            val res = PythonStaticAnalyzer.analyzeContent(fileRes.fileName, fileRes.filePath, """
                import subprocess, socket, base64
                print("Simulated token harvester")
                webhook = "https://discord.com/api/webhooks/12345/harvest"
            """.trimIndent())
            scanResult = res
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🐍 PYTHON SECURITY SCANNER", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberCanvasDark)
            )
        },
        containerColor = CyberCanvasDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CyberCard {
                    Text(
                        text = "تحليل السكربتات والأدوات البرمجية (Static Analysis)",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "قم بلصق كود بايثون (.py) أو تشغيل عينة تجريبية لفحص الاستدعاءات الحساسة (subprocess, socket, requests, ctypes, base64 obfuscation)",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = { inputCode = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("# Paste .py script code here...\nimport os, subprocess\nos.system('echo testing')", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = TitaniumBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CyberButton(
                            text = "فحص الكود",
                            onClick = { analyzePythonCode() },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Code
                        )
                        CyberButton(
                            text = "عينة اختبار 🧪",
                            onClick = { generateSimulatedScript() },
                            modifier = Modifier.weight(1f),
                            accentColor = CyberCyan,
                            isSecondary = true
                        )
                    }
                }
            }

            scanResult?.let { res ->
                item {
                    CyberCard(
                        borderColor = when (res.severity) {
                            SecuritySeverity.SAFE -> NeonEmerald
                            SecuritySeverity.SUSPICIOUS -> AmberWarning
                            SecuritySeverity.DANGEROUS, SecuritySeverity.CRITICAL -> CrimsonDanger
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = res.fileName,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Threat Score: ${res.threatScore}/100",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            ThreatBadge(severity = res.severity)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = res.summary, color = TextPrimary, fontSize = 13.sp)

                        if (res.suspiciousImports.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "📦 الاستيرادات الحساسة (Imports):", color = AmberWarning, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            res.suspiciousImports.forEach { Text(text = "• $it", color = TextSecondary, fontSize = 11.sp) }
                        }

                        if (res.dangerousCalls.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "⚠️ الاستدعاءات الخطيرة (Dangerous Calls):", color = CrimsonDanger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            res.dangerousCalls.forEach { Text(text = "• $it", color = TextSecondary, fontSize = 11.sp) }
                        }

                        if (res.netConnections.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "🌐 مؤشرات الاتصال واستخراج البيانات:", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            res.netConnections.forEach { Text(text = "• $it", color = TextSecondary, fontSize = 11.sp) }
                        }

                        if (res.severity != SecuritySeverity.SAFE) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CyberButton(
                                text = "☣️ عزل الملف وتخزين السجل الأمني",
                                onClick = {
                                    scope.launch {
                                        val mockScanRes = FileScanResult(
                                            filePath = res.filePath,
                                            fileName = res.fileName,
                                            fileSize = 1024L,
                                            mimeType = "text/x-python",
                                            sha256 = "python_mock_hash",
                                            threatScore = res.threatScore,
                                            severity = res.severity,
                                            threatName = "Python Threat: ${res.fileName}",
                                            reasons = res.dangerousCalls,
                                            fileCategory = "PYTHON"
                                        )
                                        repo.quarantineFile(mockScanRes)
                                    }
                                },
                                accentColor = CrimsonDanger
                            )
                        }
                    }
                }
            }
        }
    }
}
