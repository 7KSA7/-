package com.example.ui.screens.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.security.models.SecuritySeverity
import com.example.security.scanner.FullDeviceScanner
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.ThreatBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScanScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { SecurityRepository(context) }

    var isScanning by remember { mutableStateOf(false) }
    var scannedCount by remember { mutableIntStateOf(0) }
    var currentFile by remember { mutableStateOf("Ready to initiate scan...") }
    var scanResults by remember { mutableStateOf<List<FileScanResult>>(emptyList()) }

    fun startScan() {
        scope.launch {
            isScanning = true
            scannedCount = 0
            val results = FullDeviceScanner.performFullScan(context) { count, file ->
                scannedCount = count
                currentFile = file
            }
            scanResults = results
            isScanning = false
            currentFile = "Full device scan complete. ${results.size} items analyzed."

            // Log event
            val threats = results.count { it.severity != SecuritySeverity.SAFE }
            repo.logEvent(
                title = "Full Device Scan Executed",
                details = "Scanned $scannedCount files. Found $threats threat indicators.",
                severity = if (threats > 0) "DANGEROUS" else "SAFE",
                category = "SCANNER"
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔎 فحص كل شيء - FULL SCAN", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Control Card
            CyberCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isScanning) "جارٍ فحص النظام والملفات..." else "فحص النظام والملفات بالكامل",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentFile,
                        color = TextMuted,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (isScanning) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = NeonEmerald,
                            trackColor = TitaniumBorder
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "تم فحص $scannedCount عنصر",
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        CyberButton(
                            text = "بدء الفحص الشامل الآن",
                            onClick = { startScan() },
                            icon = Icons.Default.PlayArrow
                        )
                    }
                }
            }

            // Results List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نتائج الفحص (${scanResults.size})",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                val threatCount = scanResults.count { it.severity != SecuritySeverity.SAFE }
                if (threatCount > 0) {
                    Text(
                        text = "🚨 $threatCount تهديدات مفحوصة",
                        color = CrimsonDanger,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(scanResults) { item ->
                    CyberCard(
                        borderColor = when (item.severity) {
                            SecuritySeverity.SAFE -> TitaniumBorder
                            SecuritySeverity.SUSPICIOUS -> AmberWarning
                            SecuritySeverity.DANGEROUS, SecuritySeverity.CRITICAL -> CrimsonDanger
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.fileName,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${item.fileCategory} • ${item.fileSize / 1024} KB",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                if (item.reasons.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.reasons.joinToString("\n• "),
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                ThreatBadge(severity = item.severity)
                                if (item.severity != SecuritySeverity.SAFE) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(
                                        onClick = {
                                            scope.launch {
                                                repo.quarantineFile(item)
                                            }
                                        }
                                    ) {
                                        Text("☣️ عزل", color = CrimsonDanger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
