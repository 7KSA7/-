package com.example.ui.screens.lockdown

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.preferences.SecurityPreferences
import com.example.data.repository.SecurityRepository
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyLockdownScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { SecurityRepository(context) }
    val prefs = remember { SecurityPreferences(context) }

    val isLockdown by prefs.isEmergencyLockdownActive.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🚨 الإغلاق الطارئ (EMERGENCY LOCKDOWN)", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CyberCard(borderColor = CrimsonDanger) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonDanger, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isLockdown) "🚨 وضع الإغلاق الطارئ نـشـط!" else "وضع العزل والحظر الطارئ",
                        color = CrimsonDanger,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "عند تفعيل الإغلاق الطارئ يتم تقييد الاتصالات غير المصرح بها، وعزل أي ملف جديد يتم تحميله فوراً لمنع الاختراق المنتشر.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CyberButton(
                        text = if (isLockdown) "إلغاء وضع الإغلاق الطارئ" else "🚨 تفعيل الإغلاق الطارئ الفوري",
                        onClick = {
                            scope.launch {
                                val newState = !isLockdown
                                prefs.setEmergencyLockdown(newState)
                                repo.logEvent(
                                    title = "Emergency Lockdown Toggled",
                                    details = "Lockdown state set to: $newState",
                                    severity = if (newState) "CRITICAL" else "SAFE",
                                    category = "LOCKDOWN"
                                )
                            }
                        },
                        accentColor = CrimsonDanger
                    )
                }
            }
        }
    }
}
