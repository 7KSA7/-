package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.models.SecuritySeverity
import com.example.ui.theme.*

@Composable
fun ThreatBadge(
    severity: SecuritySeverity,
    modifier: Modifier = Modifier
) {
    val (color, glow, label) = when (severity) {
        SecuritySeverity.SAFE -> Triple(NeonEmerald, NeonEmeraldGlow, "SAFE 🟢")
        SecuritySeverity.SUSPICIOUS -> Triple(AmberWarning, AmberGlow, "SUSPICIOUS 🟡")
        SecuritySeverity.DANGEROUS -> Triple(CrimsonDanger, CrimsonGlow, "DANGEROUS 🔴")
        SecuritySeverity.CRITICAL -> Triple(CrimsonDanger, CrimsonGlow, "CRITICAL ☠️")
    }

    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(glow)
            .border(1.dp, color, shape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
