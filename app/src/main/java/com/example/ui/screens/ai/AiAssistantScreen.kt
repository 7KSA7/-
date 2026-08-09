package com.example.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.security.ai.VipAiAssistant
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER" or "VIP_AI"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(navController: NavController) {
    val scope = rememberCoroutineScope()

    var userPrompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "VIP_AI",
                message = "مرحباً بك! أنا مساعد VIP AI الأمني الذكي. يمكنني شرح التهديدات الأمنية، تحليل صلاحيات التطبيقات، وتفسير أسباب عزل السكربتات والملفات."
            )
        )
    }

    fun sendMessage() {
        val text = userPrompt.trim()
        if (text.isBlank() || isLoading) return

        messages.add(ChatMessage(sender = "USER", message = text))
        userPrompt = ""
        isLoading = true

        scope.launch {
            val reply = VipAiAssistant.analyzeSecurityQuery(text)
            messages.add(ChatMessage(sender = "VIP_AI", message = reply))
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🧠 VIP AI SECURITY ASSISTANT", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    val isUser = msg.sender == "USER"
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isUser) CyberCyanGlow else TitaniumSurface,
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isUser) "أنت" else "🧠 VIP AI",
                                    color = if (isUser) CyberCyan else NeonEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.message,
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                if (isLoading) {
                    item {
                        Text(text = "🧠 VIP AI يقوم بالتحليل الآن...", color = CyberCyan, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userPrompt,
                    onValueChange = { userPrompt = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("أسأل VIP AI عن أي أذونات أو ملفات...", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = TitaniumBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { sendMessage() },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonEmerald)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = CyberCanvasDark)
                }
            }
        }
    }
}
