package com.example.security.incident

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class IncidentLevel {
    LEVEL_1_LOW,         // Alert only
    LEVEL_2_SUSPICIOUS,  // Additional analysis + active monitoring
    LEVEL_3_HIGH,        // Block / Quarantine item
    LEVEL_4_CRITICAL     // Isolate element + Instant Alert + Incident Log + Emergency Protection
}

data class IncidentEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val level: IncidentLevel,
    val title: String,
    val description: String,
    val originLayer: String,
    val targetItem: String,
    val actionTaken: String,
    val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
)

object IncidentResponseEngine {

    private val _incidents = MutableStateFlow<List<IncidentEvent>>(
        listOf(
            IncidentEvent(
                level = IncidentLevel.LEVEL_2_SUSPICIOUS,
                title = "فحص الاتصالات الخارجية",
                description = "مراقبة نشاط خلفية خامل لخدمة إمكانية الوصول Accessibility",
                originLayer = "Behavior Engine (Layer 4)",
                targetItem = "Accessibility Monitoring",
                actionTaken = "مراقبة مكثفة وتحليل السلوك"
            ),
            IncidentEvent(
                level = IncidentLevel.LEVEL_1_LOW,
                title = "تحديث قواعد التهديدات",
                description = "تم المزامنة بنجاح مع قاعدة بيانات التهديدات عالمياً",
                originLayer = "Threat Intelligence (Layer 7)",
                targetItem = "Global Signatures DB",
                actionTaken = "تحديث القواعد الأمانية"
            )
        )
    )
    val incidents: StateFlow<List<IncidentEvent>> = _incidents.asStateFlow()

    fun reportIncident(
        level: IncidentLevel,
        title: String,
        description: String,
        originLayer: String,
        targetItem: String,
        actionTaken: String
    ) {
        val event = IncidentEvent(
            level = level,
            title = title,
            description = description,
            originLayer = originLayer,
            targetItem = targetItem,
            actionTaken = actionTaken
        )
        _incidents.value = listOf(event) + _incidents.value
    }

    fun clearIncidents() {
        _incidents.value = emptyList()
    }
}
