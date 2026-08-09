package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.models.SecuritySeverity
import com.example.ui.theme.*

@Composable
fun AnimatedSecurityShield(
    score: Int,
    statusText: String,
    severity: SecuritySeverity,
    isScanning: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val scanAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "scan"
    )

    val mainColor = when (severity) {
        SecuritySeverity.SAFE -> NeonEmerald
        SecuritySeverity.SUSPICIOUS -> AmberWarning
        SecuritySeverity.DANGEROUS, SecuritySeverity.CRITICAL -> CrimsonDanger
    }

    val mainGlow = when (severity) {
        SecuritySeverity.SAFE -> NeonEmeraldGlow
        SecuritySeverity.SUSPICIOUS -> AmberGlow
        SecuritySeverity.DANGEROUS, SecuritySeverity.CRITICAL -> CrimsonGlow
    }

    Box(
        modifier = modifier
            .size(220.dp)
            .scale(if (isScanning) pulseScale else 1f),
        contentAlignment = Alignment.Center
    ) {
        // Outer Radar & Glow Ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2 - 10.dp.toPx()

            drawCircle(
                color = mainGlow,
                radius = radius + 8.dp.toPx(),
                style = Stroke(width = 4.dp.toPx())
            )

            drawCircle(
                color = TitaniumBorder,
                radius = radius,
                style = Stroke(width = 2.dp.toPx())
            )

            if (isScanning) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(Color.Transparent, mainColor),
                        center = center
                    ),
                    startAngle = scanAngle,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = Offset(10.dp.toPx(), 10.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                )
            }
        }

        // Inner Shield Container
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(TitaniumSurface, CyberCanvasDark)
                    )
                )
                .border(2.dp, mainColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (severity == SecuritySeverity.SAFE) Icons.Default.Shield else Icons.Default.Warning,
                    contentDescription = "Shield Icon",
                    tint = mainColor,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$score/100",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = statusText,
                    color = mainColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
