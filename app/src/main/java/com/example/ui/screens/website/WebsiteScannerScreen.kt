package com.example.ui.screens.website

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.security.ai.VipAiAssistant
import com.example.security.network.WebsiteScanResult
import com.example.security.network.WebsiteSafetyStatus
import com.example.security.network.WebsiteSecurityScanner
import com.example.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebsiteScannerScreen(navController: NavController) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    var inputUrl by remember { mutableStateOf("https://example.com") }
    var isScanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<WebsiteScanResult?>(null) }
    var showWarningDialog by remember { mutableStateOf(false) }

    val recentUrls = remember {
        listOf(
            "https://example.com",
            "bit.ly/claim-crypto-bonus",
            "https://mybank-login-security.xyz",
            "https://google.com"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "🌐 فحص المواقع والروابط",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
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
            // Header Description Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF3B82F6)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "فحص متقدم للرابط بدون فتح مباشر",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "تحليل تشفير TLS، كشف صفحات التصيد والاحتيال، كشف تحويلات الروابط المختصرة (Shortened URLs) والتأكد من سلامة الخوادم قبل الزيارة.",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Input Box Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "أدخل الرابط المراد فحصه:",
                            color = Color(0xFFE2E8F0),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        OutlinedTextField(
                            value = inputUrl,
                            onValueChange = { inputUrl = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("https://example.com", color = Color(0xFF64748B)) },
                            leadingIcon = {
                                Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            trailingIcon = {
                                Row {
                                    if (inputUrl.isNotBlank()) {
                                        IconButton(onClick = { inputUrl = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "مسح", tint = Color(0xFF94A3B8))
                                        }
                                    }
                                    IconButton(onClick = {
                                        clipboardManager.getText()?.text?.let { pasted ->
                                            inputUrl = pasted
                                        }
                                    }) {
                                        Icon(Icons.Default.ContentPaste, contentDescription = "لصق", tint = Color(0xFF38BDF8))
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    if (inputUrl.isNotBlank()) {
                                        coroutineScope.launch {
                                            isScanning = true
                                            scanResult = WebsiteSecurityScanner.scanWebsite(inputUrl)
                                            isScanning = false
                                        }
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                keyboardController?.hide()
                                if (inputUrl.isNotBlank()) {
                                    coroutineScope.launch {
                                        isScanning = true
                                        scanResult = WebsiteSecurityScanner.scanWebsite(inputUrl)
                                        isScanning = false
                                        scanResult?.let { res ->
                                            if (res.status != WebsiteSafetyStatus.SAFE) {
                                                showWarningDialog = true
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isScanning && inputUrl.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6),
                                disabledContainerColor = Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري تحليل الموقع والشهادات...", color = Color.White)
                            } else {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🔍 SCAN WEBSITE (فحص الموقع)", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Preset URLs
            item {
                Column {
                    Text(
                        text = "روابط سريعة للتجربة:",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        recentUrls.take(2).forEach { url ->
                            SuggestionChip(
                                onClick = {
                                    inputUrl = url
                                    coroutineScope.launch {
                                        isScanning = true
                                        scanResult = WebsiteSecurityScanner.scanWebsite(url)
                                        isScanning = false
                                    }
                                },
                                label = { Text(url, color = Color(0xFF38BDF8), fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1E293B)),
                                border = SuggestionChipDefaults.suggestionChipBorder(borderColor = Color(0xFF334155), enabled = true)
                            )
                        }
                    }
                }
            }

            // Scan Results Display
            scanResult?.let { result ->
                item {
                    val statusColor = when (result.status) {
                        WebsiteSafetyStatus.SAFE -> Color(0xFF22C55E)
                        WebsiteSafetyStatus.SUSPICIOUS -> Color(0xFFEAB308)
                        WebsiteSafetyStatus.DANGEROUS -> Color(0xFFEF4444)
                    }

                    val statusText = when (result.status) {
                        WebsiteSafetyStatus.SAFE -> "SAFE (آمن وموثوق) 🟢"
                        WebsiteSafetyStatus.SUSPICIOUS -> "SUSPICIOUS (مشبوه) 🟡"
                        WebsiteSafetyStatus.DANGEROUS -> "DANGEROUS (خطير جداً) 🔴"
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = result.domain,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "وقت الفحص: ${result.scanTimestamp}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                }

                                Surface(
                                    color = statusColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
                                ) {
                                    Text(
                                        text = "${result.score} / 100",
                                        color = statusColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color(0xFF334155))

                            Text(
                                text = "حالة الأمان: $statusText",
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            // Properties List
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                CheckDetailItem(
                                    title = "بروتوكول التشفير (HTTPS/TLS)",
                                    value = if (result.isHttps) "مُشفر بمعايير HTTPS" else "غير مشفر (HTTP عادي)",
                                    isOk = result.isHttps
                                )
                                CheckDetailItem(
                                    title = "الرابط النهائي وقنوات التحويل",
                                    value = if (result.redirectCount > 0) "${result.redirectCount} تحويلات -> ${result.finalResolvedUrl}" else "مباشر بدون تحويلات خفية",
                                    isOk = result.redirectCount < 3
                                )
                                CheckDetailItem(
                                    title = "حالة النطاق والسمعة (Domain Age)",
                                    value = result.domainAgeStatus,
                                    isOk = !result.isKnownMaliciousDomain
                                )
                                CheckDetailItem(
                                    title = "مؤشرات التصيد والاحتيال (Phishing)",
                                    value = if (result.hasPhishingIndicators) "تم رصد أنماط أسماء نطاقات مضللة" else "خالٍ من مؤشرات صفحات تسجيل الدخول المزيفة",
                                    isOk = !result.hasPhishingIndicators
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "تفاصيل تقرير الفحص الأمني:",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            result.riskReasons.forEach { reason ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Text("• ", color = statusColor, fontWeight = FontWeight.Bold)
                                    Text(reason, color = Color(0xFFCBD5E1), fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action Buttons inside Scan Result
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        navController.navigate(Screen.AiAssistant.route)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تحليل VIP AI", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.finalResolvedUrl))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Handle invalid URL
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = statusColor),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("فتح الرابط", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Warning Dialog for Dangerous / Suspicious Websites
    if (showWarningDialog && scanResult != null) {
        val result = scanResult!!
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "🚨 WARNING - تحذير أمني شديد الخطورة",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "كشف VIP Protection أن الرابط (${result.domain}) قد يكون خطيراً أو مضللاً.",
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "الأسباب الرئيسية:",
                        color = Color(0xFFF87171),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    result.riskReasons.forEach { r ->
                        Text("• $r", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showWarningDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text("GO BACK (التراجع الفوري)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showWarningDialog = false
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.finalResolvedUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) { }
                    }
                ) {
                    Text("OPEN ANYWAY (فتح على أي حال)", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun CheckDetailItem(title: String, value: String, isOk: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = if (isOk) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (isOk) Color(0xFF22C55E) else Color(0xFFEF4444),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(title, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
        Text(
            text = value,
            color = if (isOk) Color(0xFFE2E8F0) else Color(0xFFF87171),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
