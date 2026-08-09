package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.security.engine.VipSecurityEngine
import com.example.security.models.SecuritySeverity
import com.example.ui.components.AnimatedSecurityShield
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

data class QuickFeatureTile(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val badge: String? = null
)

@Composable
fun DashboardScreen(
    navController: NavController
) {
    val engineStatus by VipSecurityEngine.status.collectAsState()

    val quickTiles = listOf(
        QuickFeatureTile("مركز الأمان", "Device Security Center", Icons.Default.Dashboard, Screen.SecurityCenter.route, "📊"),
        QuickFeatureTile("EXTREME DEFENSE", "11 Layers Active Control", Icons.Default.Shield, Screen.ExtremeDefense.route, "🛡️"),
        QuickFeatureTile("فحص المواقع", "Website & Link Scanner", Icons.Default.Public, Screen.WebsiteScanner.route, "🌐"),
        QuickFeatureTile("فحص كل شيء", "Full Device Scan", Icons.Default.Search, Screen.FullScan.route, "PRO"),
        QuickFeatureTile("فاحص بايثون", "Python Static Analyzer", Icons.Default.Code, Screen.PythonScan.route, "🐍"),
        QuickFeatureTile("درع الخصوصية", "Anti-Spyware Audit", Icons.Default.Security, Screen.PrivacyShield.route),
        QuickFeatureTile("حماية الشبكة", "Network Shield & VPN", Icons.Default.CellTower, Screen.NetworkProtection.route),
        QuickFeatureTile("الخزنة المشفرة", "AES-256 Secure Vault", Icons.Default.Lock, Screen.SecureVault.route),
        QuickFeatureTile("المحجر الصحي", "Quarantine System", Icons.Default.Biotech, Screen.Quarantine.route),
        QuickFeatureTile("مساعد VIP AI", "Gemini Security Assistant", Icons.Default.Psychology, Screen.AiAssistant.route, "AI"),
        QuickFeatureTile("سجل الأحداث", "Security History Logs", Icons.Default.History, Screen.SecurityLogs.route),
        QuickFeatureTile("إغلاق طارئ", "Emergency Lockdown", Icons.Default.Warning, Screen.EmergencyLockdown.route, "🚨")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberCanvasDark)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🛡️ VIP PROTECTION",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "YOUR PERSONAL SECURITY COMMAND CENTER",
                    color = NeonEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = { navController.navigate(Screen.Settings.route) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(TitaniumCard)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextPrimary
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Shield Section
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val severity = if (engineStatus.protectionScore >= 80) SecuritySeverity.SAFE else if (engineStatus.protectionScore >= 50) SecuritySeverity.SUSPICIOUS else SecuritySeverity.CRITICAL
                    val statusText = if (engineStatus.isExtremeModeActive) "EXTREME DEFENSE ACTIVE 🛡️" else "DEFENSE ACTIVE"
                    
                    AnimatedSecurityShield(
                        score = engineStatus.protectionScore,
                        statusText = statusText,
                        severity = severity
                    )
                }
            }

            // Quick Primary Scan Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CyberButton(
                        text = "🌐 فحص المواقع",
                        onClick = { navController.navigate(Screen.WebsiteScanner.route) },
                        modifier = Modifier.weight(1f)
                    )
                    CyberButton(
                        text = "📊 مركز الأمان",
                        onClick = { navController.navigate(Screen.SecurityCenter.route) },
                        modifier = Modifier.weight(1f),
                        accentColor = CyberCyan,
                        isSecondary = true
                    )
                }
            }

            // Status Check list
            item {
                CyberCard(
                    onClick = { navController.navigate(Screen.ExtremeDefense.route) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXTREME DEFENSE ENGINE (11 Layers Active)",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberCyan)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    engineStatus.layers.take(4).forEach { layer ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Layer ${layer.layerNumber}: ${layer.nameAr}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (layer.isEnabled) "✓ نشط" else "✕ معطّل",
                                color = if (layer.isEnabled) NeonEmerald else CrimsonDanger,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Quick Access Security Modules Title
            item {
                Text(
                    text = "وحدات الحماية والتحكم (Security Command Modules)",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Quick Access Security Modules Grid
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    quickTiles.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { tile ->
                                CyberCard(
                                    modifier = Modifier.weight(1f),
                                    onClick = { navController.navigate(tile.route) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = tile.icon,
                                            contentDescription = null,
                                            tint = CyberCyan,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = tile.title,
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = tile.subtitle,
                                                color = TextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
