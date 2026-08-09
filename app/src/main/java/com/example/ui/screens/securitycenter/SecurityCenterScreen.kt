package com.example.ui.screens.securitycenter

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.security.engine.VipSecurityEngine
import com.example.ui.navigation.Screen

data class SmartRecommendation(
    val id: String,
    val title: String,
    val description: String,
    val severityColor: Color,
    val actionText: String,
    val actionRoute: String? = null,
    val isSystemSettingsAction: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityCenterScreen(navController: NavController) {
    val context = LocalContext.current
    val engineState by VipSecurityEngine.status.collectAsState()

    val smartRecommendations = remember {
        listOf(
            SmartRecommendation(
                id = "1",
                title = "تطبيق يملك صلاحية الميكروفون بشكل مستمر",
                description = "تم رصد وصول خلفي خامل لإذن التسجيل والصوت في أحد التطبيقات الثانوية.",
                severityColor = Color(0xFFEAB308),
                actionText = "OPEN APP PERMISSIONS",
                isSystemSettingsAction = true
            ),
            SmartRecommendation(
                id = "2",
                title = "زيارة رابط تم تحويله عبر اختصار مشبوه",
                description = "ينصح باستخدام فاحص المواقع قبل دخول الروابط لتفادي صائدات كلمات المرور.",
                severityColor = Color(0xFF38BDF8),
                actionText = "SCAN WEBSITE",
                actionRoute = Screen.WebsiteScanner.route
            ),
            SmartRecommendation(
                id = "3",
                title = "ملف معزول بحاجة لمراجعة في المحجر الصحي",
                description = "يوجد ملف تم عزله لمنع التشغيل لارتفاع نسبة التشويش للتنفيذيين.",
                severityColor = Color(0xFFF97316),
                actionText = "VIEW IN QUARANTINE",
                actionRoute = Screen.Quarantine.route
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "📊 DEVICE SECURITY CENTER",
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
            // Overall Device Status Hero Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DEVICE SECURITY",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "الحالة العامة للنظام",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                color = Color(0xFF22C55E).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFF22C55E), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Protection: ACTIVE",
                                        color = Color(0xFF22C55E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Grid of metrics
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatCell(title = "التهديدات النشطة (Threats)", value = "0", color = Color(0xFF22C55E))
                                StatCell(title = "تطبيقات مشبوهة (Suspicious Apps)", value = "2", color = Color(0xFFEAB308))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatCell(title = "نطاقات محجوبة (Blocked Domains)", value = "${engineState.blockedDomainsCount}", color = Color(0xFF38BDF8))
                                StatCell(title = "ملفات المحجر (Quarantined Files)", value = "${engineState.quarantinedFilesCount}", color = Color(0xFFF97316))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatCell(title = "مخاطر الخصوصية (Privacy Risks)", value = "3", color = Color(0xFFEC4899))
                                StatCell(title = "آخر فحص (Last Scan)", value = engineState.lastScanTime, color = Color(0xFFCBD5E1))
                            }
                        }
                    }
                }
            }

            // Quick Module Launchers Grid
            item {
                Text(
                    text = "وحدات الدفاع السريعة:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickModuleTile(
                        title = "EXTREME DEFENSE",
                        subtitle = "11 Layers Active",
                        icon = Icons.Default.Shield,
                        accentColor = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.ExtremeDefense.route) }
                    )
                    QuickModuleTile(
                        title = "فحص المواقع",
                        subtitle = "Website Scanner",
                        icon = Icons.Default.Public,
                        accentColor = Color(0xFF22C55E),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.WebsiteScanner.route) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickModuleTile(
                        title = "حماية الشبكة",
                        subtitle = "Network Shield",
                        icon = Icons.Default.CellTower,
                        accentColor = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.NetworkProtection.route) }
                    )
                    QuickModuleTile(
                        title = "فحص الجهاز",
                        subtitle = "Full Device Scan",
                        icon = Icons.Default.QrCodeScanner,
                        accentColor = Color(0xFFEC4899),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.FullScan.route) }
                    )
                }
            }

            // Smart Security Recommendations Section
            item {
                Text(
                    text = "🧠 TACTICAL RECOMMENDATIONS (التوصيات الأمنية الذكية):",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(smartRecommendations) { rec ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = rec.severityColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = rec.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = rec.description,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )

                        Button(
                            onClick = {
                                if (rec.isSystemSettingsAction) {
                                    try {
                                        val intent = Intent(Settings.ACTION_APPLICATION_SETTINGS)
                                        context.startActivity(intent)
                                    } catch (e: Exception) { }
                                } else if (rec.actionRoute != null) {
                                    navController.navigate(rec.actionRoute)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(rec.actionText, color = rec.severityColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCell(title: String, value: String, color: Color) {
    Column {
        Text(title, color = Color(0xFF94A3B8), fontSize = 11.sp)
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun QuickModuleTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(subtitle, color = Color(0xFF64748B), fontSize = 10.sp)
            }
        }
    }
}
