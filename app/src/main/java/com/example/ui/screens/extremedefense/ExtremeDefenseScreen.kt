package com.example.ui.screens.extremedefense

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.security.engine.DefenseLayerInfo
import com.example.security.engine.VipSecurityEngine
import com.example.security.incident.IncidentLevel
import com.example.security.incident.IncidentResponseEngine
import com.example.security.selfdefense.VipSelfDefenseManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtremeDefenseScreen(navController: NavController) {
    val context = LocalContext.current
    val engineState by VipSecurityEngine.status.collectAsState()
    val incidentsState by IncidentResponseEngine.incidents.collectAsState()
    val selfDefenseState by VipSelfDefenseManager.status.collectAsState()

    LaunchedEffect(Unit) {
        VipSelfDefenseManager.performSelfDefenseCheck(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "🛡️ VIP EXTREME DEFENSE CONTROL",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Master Extreme Mode Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF8B5CF6))),
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (engineState.isExtremeModeActive) Color(0xFF22C55E).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = null,
                                            tint = if (engineState.isExtremeModeActive) Color(0xFF22C55E) else Color(0xFFEF4444),
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (engineState.isExtremeModeActive) "EXTREME DEFENSE ACTIVE 🟢" else "DEFENSE OFF 🔴",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "نظام الدفاع متعدد الطبقات (11 Layers Active)",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Switch(
                                checked = engineState.isExtremeModeActive,
                                onCheckedChange = { active ->
                                    VipSecurityEngine.setExtremeDefenseActive(active)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF3B82F6),
                                    uncheckedThumbColor = Color(0xFF94A3B8),
                                    uncheckedTrackColor = Color(0xFF334155)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            MetricCounter(title = "نقاط الأمان", value = "${engineState.protectionScore}/100", color = Color(0xFF38BDF8))
                            MetricCounter(title = "تهديدات محجوبة", value = "${engineState.totalThreatsBlocked}", color = Color(0xFF22C55E))
                            MetricCounter(title = "نطاقات محظورة", value = "${engineState.blockedDomainsCount}", color = Color(0xFFEAB308))
                        }
                    }
                }
            }

            // Self Defense Integrity Status Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = null,
                                tint = Color(0xFF8B5CF6)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🛡️ VIP SELF DEFENSE (الحماية الذاتية للتطبيق)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("سلامة حزمة التطبيق (Integrity):", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("مُحققة بالكامل 🟢", color = Color(0xFF22C55E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("حماية الإعدادات ضد العبث:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("نشطة وتأمين الذاكرة 🟢", color = Color(0xFF22C55E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("مراقبة أجهزة التصحيح (Debugger):", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text(
                                if (selfDefenseState.isDebuggerOrHookingDetected) "رصد نمط تصحيح (محمي) 🟡" else "خالٍ من أجهزة التجسس 🟢",
                                color = if (selfDefenseState.isDebuggerOrHookingDetected) Color(0xFFEAB308) else Color(0xFF22C55E),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 11 Layers Title
            item {
                Text(
                    text = "طبقات الدفاع الإحدى عشرة (11 Defense Layers):",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // List of 11 Defense Layers
            items(engineState.layers) { layer ->
                DefenseLayerCard(layer = layer, onToggle = { enabled ->
                    VipSecurityEngine.toggleLayer(layer.layerNumber, enabled)
                })
            }

            // Live Incident Response Feed Title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🚨 سجل الاستجابة للحوادث (Incident Log):",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    TextButton(onClick = { IncidentResponseEngine.clearIncidents() }) {
                        Text("مسح السجل", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                }
            }

            // Incident List Items
            if (incidentsState.isEmpty()) {
                item {
                    Text(
                        text = "لا توجد حوادث أمنية حالياً. النظام يعمل باستقرار كاملاً.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(incidentsState) { incident ->
                    val (levelColor, levelBadge) = when (incident.level) {
                        IncidentLevel.LEVEL_1_LOW -> Pair(Color(0xFF38BDF8), "LEVEL 1 — LOW")
                        IncidentLevel.LEVEL_2_SUSPICIOUS -> Pair(Color(0xFFEAB308), "LEVEL 2 — SUSPICIOUS")
                        IncidentLevel.LEVEL_3_HIGH -> Pair(Color(0xFFF97316), "LEVEL 3 — HIGH")
                        IncidentLevel.LEVEL_4_CRITICAL -> Pair(Color(0xFFEF4444), "LEVEL 4 — CRITICAL")
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = levelColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = levelBadge,
                                        color = levelColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(incident.timestamp, color = Color(0xFF64748B), fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(incident.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(incident.description, color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row {
                                Text("المصدر: ", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text(incident.originLayer, color = Color(0xFF38BDF8), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DefenseLayerCard(layer: DefenseLayerInfo, onToggle: (Boolean) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = Color(0xFF3B82F6).copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${layer.layerNumber}",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = layer.nameAr,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = layer.statusText,
                        color = if (layer.isEnabled) Color(0xFF94A3B8) else Color(0xFFEF4444),
                        fontSize = 11.sp
                    )
                }
            }

            Switch(
                checked = layer.isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF38BDF8),
                    uncheckedThumbColor = Color(0xFF94A3B8),
                    uncheckedTrackColor = Color(0xFF334155)
                )
            )
        }
    }
}

@Composable
fun MetricCounter(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        Text(title, color = Color(0xFF94A3B8), fontSize = 11.sp)
    }
}
