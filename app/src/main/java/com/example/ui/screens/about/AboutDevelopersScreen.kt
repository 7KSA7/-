package com.example.ui.screens.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDevelopersScreen(navController: NavController) {
    val context = LocalContext.current

    fun openTelegram(handle: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/${handle.removePrefix("@")}"))
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("👥 المطورون والدعم الفني (DEVELOPERS)", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
            CyberCard {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(TitaniumSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Group, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(38.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "🛡️ VIP PROTECTION",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "التطبيق المعياري المتقدم للحماية والأمن الرقمي",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "تم بناء وتطوير هذا التطبيق بواسطة الفريق الهندسي:\nVO + QASSAM",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "حسابات التواصل والدعم الرسمي على Telegram",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            CyberCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "المطور VO", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Telegram: @VO_HQQ", color = CyberCyan, fontSize = 13.sp)
                    }

                    CyberButton(
                        text = "تواصل ✈️",
                        onClick = { openTelegram("@VO_HQQ") },
                        modifier = Modifier.width(100.dp),
                        accentColor = CyberCyan
                    )
                }
            }

            CyberCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "المطور QASSAM", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Telegram: @l1_73", color = NeonEmerald, fontSize = 13.sp)
                    }

                    CyberButton(
                        text = "تواصل ✈️",
                        onClick = { openTelegram("@l1_73") },
                        modifier = Modifier.width(100.dp),
                        accentColor = NeonEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "VIP PROTECTION v1.0 • Built with Kotlin, Jetpack Compose, KSP & Room",
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
