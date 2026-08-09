package com.example.ui.screens.network

import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Public
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
import com.example.security.network.NetworkProtectionManager
import com.example.security.network.VipVpnService
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkProtectionScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { SecurityRepository(context) }

    var isVpnActive by remember { mutableStateOf(false) }
    val blockedCount by NetworkProtectionManager.blockedDomainsCount.collectAsState()
    val connectionLogs by NetworkProtectionManager.connectionLogs.collectAsState()

    val vpnLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val intent = Intent(context, VipVpnService::class.java)
        context.startService(intent)
        isVpnActive = true
    }

    fun toggleVpn() {
        if (!isVpnActive) {
            val vpnIntent = VpnService.prepare(context)
            if (vpnIntent != null) {
                vpnLauncher.launch(vpnIntent)
            } else {
                val intent = Intent(context, VipVpnService::class.java)
                context.startService(intent)
                isVpnActive = true
            }
        } else {
            val intent = Intent(context, VipVpnService::class.java).apply {
                action = VipVpnService.ACTION_STOP
            }
            context.startService(intent)
            isVpnActive = false
        }
    }

    fun simulateConnAttempt() {
        NetworkProtectionManager.logConnectionAttempt(
            context = context,
            appName = "UnknownApp.apk",
            packageName = "com.suspicious.app",
            destinationHost = "malware-command-control.xyz",
            destinationIp = "185.220.101.5"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌐 NETWORK SHIELD & VPN PROTECTION", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
                CyberCard(
                    borderColor = if (isVpnActive) NeonEmerald else TitaniumBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "حماية الشبكة وتصفية النطاقات (VPN)", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (isVpnActive) "🟢 درع الشبكة مفعل وتيار البيانات محمي" else "🔴 درع الشبكة غير متصل",
                                color = if (isVpnActive) NeonEmerald else CrimsonDanger,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = isVpnActive,
                            onCheckedChange = { toggleVpn() },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonEmerald)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$blockedCount", color = CrimsonDanger, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text(text = "نطاق ضار محظور", color = TextMuted, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Cloudflare / Secure DNS", color = CyberCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "نوع الاتصال المشفر", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CyberButton(
                        text = "محاكاة خادم ضار للتجربة 🧪",
                        onClick = { simulateConnAttempt() },
                        accentColor = CyberCyan,
                        isSecondary = true
                    )
                }
            }

            item {
                Text(
                    text = "سجل الاتصالات الشبكية المباشرة (${connectionLogs.size})",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(connectionLogs) { log ->
                CyberCard(
                    borderColor = if (log.isBlocked) CrimsonDanger else TitaniumBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = log.destinationHost, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${log.appName} (${log.destinationIp})", color = TextMuted, fontSize = 10.sp)
                        }
                        Text(
                            text = if (log.isBlocked) "BLOCKED 🔴" else "ALLOWED 🟢",
                            color = if (log.isBlocked) CrimsonDanger else NeonEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
