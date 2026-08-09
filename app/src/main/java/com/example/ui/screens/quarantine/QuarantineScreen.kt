package com.example.ui.screens.quarantine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
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
import com.example.security.testing.SimulatedThreatEnvironment
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuarantineScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { SecurityRepository(context) }

    val quarantineItems by repo.allQuarantineItems.collectAsState(initial = emptyList())

    fun triggerTestQuarantine() {
        scope.launch {
            val testScan = SimulatedThreatEnvironment.generateEicarTestFile(context)
            repo.quarantineFile(testScan)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("☣️ QUARANTINE SYSTEM (المحجر الصحي)", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Biotech, contentDescription = null, tint = CrimsonDanger, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "المحجر الصحي وعزل التهديدات", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "عزل الملفات المشبوهة وتغيير امتدادها لمنع تنفيذها بالخطأ مع حفظ بيانات الفحص", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    CyberButton(
                        text = "إنشاء تهديد وهمي وعزله للاختبار 🧪",
                        onClick = { triggerTestQuarantine() },
                        accentColor = CrimsonDanger,
                        isSecondary = true
                    )
                }
            }

            item {
                Text(
                    text = "الملفات المعزولة داخل المحجر (${quarantineItems.size})",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(quarantineItems) { item ->
                CyberCard(borderColor = CrimsonDanger) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.originalFileName, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Score: ${item.threatScore}/100", color = CrimsonDanger, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "التهديد: ${item.threatName}", color = AmberWarning, fontSize = 11.sp)
                        Text(text = "الأسباب: ${item.detectionReasonsJson}", color = TextMuted, fontSize = 10.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    scope.launch { repo.restoreQuarantineItem(item) }
                                }
                            ) {
                                Icon(Icons.Default.Restore, contentDescription = "Restore", tint = NeonEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("استعادة File", color = NeonEmerald, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            TextButton(
                                onClick = {
                                    scope.launch { repo.deleteQuarantineItemPermanently(item) }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonDanger, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("حذف نهائي", color = CrimsonDanger, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
