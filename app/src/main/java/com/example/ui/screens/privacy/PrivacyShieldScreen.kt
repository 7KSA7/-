package com.example.ui.screens.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.security.models.PrivacyRiskInfo
import com.example.security.privacy.AntiSpywareAnalyzer
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyShieldScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var privacyList by remember { mutableStateOf<List<PrivacyRiskInfo>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            val list = withContext(Dispatchers.IO) {
                AntiSpywareAnalyzer.scanAllAppsPrivacy(context)
            }
            privacyList = list
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🕵️ ANTI-SPYWARE & PRIVACY AUDIT", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
            CyberCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "درع الخصوصية ومنع التجسس", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "فحص الأذونات الحساسة للتطبيقات (الكاميرا، الميكروفون، الموقع، SMS، خدمة إمكانية الوصول)", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonEmerald)
                }
            } else {
                Text(
                    text = "التطبيقات التي تمت مراجعتها (${privacyList.size})",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(privacyList) { item ->
                        CyberCard(
                            borderColor = when {
                                item.privacyScore > 60 -> CrimsonDanger
                                item.privacyScore > 30 -> AmberWarning
                                else -> TitaniumBorder
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.appName, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = item.packageName, color = TextMuted, fontSize = 10.sp)
                                }
                                Text(
                                    text = item.riskLevel,
                                    color = if (item.privacyScore > 50) CrimsonDanger else NeonEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (item.cameraAccess) Text(text = "📷 الكاميرا", color = AmberWarning, fontSize = 10.sp)
                                if (item.micAccess) Text(text = "🎙️ المايك", color = CrimsonDanger, fontSize = 10.sp)
                                if (item.locationAccess) Text(text = "📍 الموقع", color = CyberCyan, fontSize = 10.sp)
                                if (item.smsAccess) Text(text = "💬 SMS", color = CrimsonDanger, fontSize = 10.sp)
                                if (item.accessibilityAccess) Text(text = "♿ Accessibility", color = CrimsonDanger, fontSize = 10.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = item.detailedSummary, color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
