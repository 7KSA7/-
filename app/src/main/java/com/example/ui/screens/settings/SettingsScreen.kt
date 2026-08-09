package com.example.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { SecurityPreferences(context) }

    val isAbsolute by prefs.isAbsoluteProtectionEnabled.collectAsState(initial = true)
    val isRealtime by prefs.isRealtimeProtectionEnabled.collectAsState(initial = true)
    val isNetwork by prefs.isNetworkProtectionEnabled.collectAsState(initial = false)
    val isPrivacy by prefs.isPrivacyShieldEnabled.collectAsState(initial = true)
    val isAntiSpyware by prefs.isAntiSpywareEnabled.collectAsState(initial = true)
    val lang by prefs.currentLanguage.collectAsState(initial = "ar")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚙️ إعدادات الحماية (SETTINGS)", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
                    Text(text = "خيارات محرك الأمان الرئيسي", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    val settingsItems = listOf(
                        "الحماية المطلقة (Absolute Protection)" to isAbsolute,
                        "محرك الفحص الحقيقي (Real-Time Protection)" to isRealtime,
                        "تصفية اتصالات VPN (Network Shield)" to isNetwork,
                        "درع الخصوصية (Privacy Shield)" to isPrivacy,
                        "نظام عدم التجسس (Anti-Spyware Engine)" to isAntiSpyware
                    )

                    settingsItems.forEachIndexed { idx, (title, state) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = title, color = TextPrimary, fontSize = 13.sp)
                            Switch(
                                checked = state,
                                onCheckedChange = { val check = it
                                    scope.launch {
                                        when (idx) {
                                            0 -> prefs.setAbsoluteProtection(check)
                                            1 -> prefs.setRealtimeProtection(check)
                                            2 -> prefs.setNetworkProtection(check)
                                            3 -> prefs.setPrivacyShield(check)
                                            4 -> prefs.setAntiSpyware(check)
                                        }
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonEmerald)
                            )
                        }
                    }
                }
            }

            item {
                CyberCard {
                    Text(text = "لغة الواجهة (Language)", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        FilterChip(
                            selected = lang == "ar",
                            onClick = { scope.launch { prefs.setLanguage("ar") } },
                            label = { Text("🇸🇦 العربية") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonEmerald, selectedLabelColor = CyberCanvasDark)
                        )
                        FilterChip(
                            selected = lang == "en",
                            onClick = { scope.launch { prefs.setLanguage("en") } },
                            label = { Text("🇺🇸 English") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberCyan, selectedLabelColor = CyberCanvasDark)
                        )
                        FilterChip(
                            selected = lang == "fr",
                            onClick = { scope.launch { prefs.setLanguage("fr") } },
                            label = { Text("🇫🇷 Français") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberCyan, selectedLabelColor = CyberCanvasDark)
                        )
                    }
                }
            }
        }
    }
}
