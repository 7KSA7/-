package com.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.security.realtime.VipForegroundService
import com.example.ui.navigation.VipNavGraph
import com.example.ui.theme.VipProtectionTheme
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : ComponentActivity() {

    private val appErrorState = mutableStateOf<Throwable?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            Handler(Looper.getMainLooper()).post {
                appErrorState.value = throwable
            }
        }

        enableEdgeToEdge()

        try {
            VipForegroundService.startService(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            VipProtectionTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val error = appErrorState.value
                    if (error != null) {
                        CrashReportScreen(
                            throwable = error,
                            onDismiss = { appErrorState.value = null }
                        )
                    } else {
                        val navController = rememberNavController()
                        VipNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun CrashReportScreen(throwable: Throwable, onDismiss: () -> Unit) {
    val sw = StringWriter()
    throwable.printStackTrace(PrintWriter(sw))
    val stackTraceString = sw.toString()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0F172A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⚠️ تم التقاط استثناء ومنع الإغلاق المفاجئ",
                color = Color(0xFFF87171),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "قام معالج الأخطاء بحماية التطبيق وعرض التقرير البرمجي:",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp),
                color = Color(0xFF1E293B),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stackTraceString,
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
            ) {
                Text("إعادة فتح الشاشة الرئيسية", color = Color.White)
            }
        }
    }
}
