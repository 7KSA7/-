package com.example.ui.screens.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.database.entities.VaultEntity
import com.example.data.repository.SecurityRepository
import com.example.security.encryption.SecureVaultManager
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureVaultScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { SecurityRepository(context) }

    val vaultItems by repo.allVaultItems.collectAsState(initial = emptyList())

    var vaultPass by remember { mutableStateOf("") }
    var fileNameToEncrypt by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("AES-256-GCM Vault Status: Active") }

    fun createSampleAndEncrypt() {
        if (vaultPass.isBlank()) {
            statusMessage = "رجاءً أدخل كلمة مرور للتشفير أولاً!"
            return
        }

        scope.launch {
            val name = if (fileNameToEncrypt.isNotBlank()) fileNameToEncrypt else "private_document_${System.currentTimeMillis()}.txt"
            val testFile = File(context.filesDir, name).apply { writeText("Confidential Data Encrypted with AES-256-GCM inside VIP Protection Vault.") }

            SecureVaultManager.encryptFile(
                context = context,
                inputFile = testFile,
                password = vaultPass.toCharArray()
            ) { encFile, success, msg ->
                statusMessage = msg
                if (success) {
                    scope.launch {
                        repo.saveVaultItem(
                            VaultEntity(
                                originalName = testFile.name,
                                encryptedName = encFile.name,
                                encryptedPath = encFile.absolutePath,
                                originalPath = testFile.absolutePath,
                                fileSize = testFile.length(),
                                mimeType = "text/plain"
                            )
                        )
                        repo.logEvent(
                            title = "File Encrypted in Vault",
                            details = "Encrypted ${testFile.name} with AES-256-GCM",
                            severity = "SAFE",
                            category = "VAULT",
                            target = encFile.name
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔐 AES-256 SECURE VAULT", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
                    Text(text = "تشفير الملفات العسكري (AES-256-GCM)", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "اشتقاق المفاتيح عبر PBKDF2/Android Keystore وإنشاء ملفات مشفرة .vipsecure", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = vaultPass,
                        onValueChange = { vaultPass = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("كلمة المرور المشفرة", color = TextMuted) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonEmerald,
                            unfocusedBorderColor = TitaniumBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fileNameToEncrypt,
                        onValueChange = { fileNameToEncrypt = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("اسم الملف للتشفير (مثال: my_passwords.txt)", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonEmerald,
                            unfocusedBorderColor = TitaniumBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CyberButton(
                        text = "🔐 تشفير وإنشاء ملف .vipsecure",
                        onClick = { createSampleAndEncrypt() },
                        icon = Icons.Default.Lock
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = statusMessage, color = CyberCyan, fontSize = 11.sp)
                }
            }

            item {
                Text(text = "الملفات المشفرة داخل الخزنة (${vaultItems.size})", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            items(vaultItems) { item ->
                CyberCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.encryptedName, color = NeonEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "الأصل: ${item.originalName} (${item.fileSize} B)", color = TextMuted, fontSize = 10.sp)
                        }

                        IconButton(
                            onClick = {
                                if (vaultPass.isBlank()) {
                                    statusMessage = "أدخل كلمة المرور لفك التشفير!"
                                    return@IconButton
                                }
                                SecureVaultManager.decryptFile(
                                    context = context,
                                    encryptedFile = File(item.encryptedPath),
                                    password = vaultPass.toCharArray(),
                                    outputDir = context.filesDir
                                ) { decFile, success, msg ->
                                    statusMessage = msg
                                }
                            }
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = "Decrypt", tint = CyberCyan)
                        }
                    }
                }
            }
        }
    }
}
