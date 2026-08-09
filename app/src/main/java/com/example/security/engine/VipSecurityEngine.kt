package com.example.security.engine

import com.example.security.models.SecuritySeverity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DefenseLayerInfo(
    val layerNumber: Int,
    val nameAr: String,
    val nameEn: String,
    val isEnabled: Boolean,
    val statusText: String,
    val threatsBlockedCount: Int
)

data class ExtremeDefenseStatus(
    val isExtremeModeActive: Boolean = true,
    val protectionScore: Int = 98,
    val totalThreatsBlocked: Int = 34,
    val suspiciousAppsCount: Int = 2,
    val blockedDomainsCount: Int = 7,
    val quarantinedFilesCount: Int = 1,
    val privacyRisksCount: Int = 3,
    val lastScanTime: String = "اليوم 15:42",
    val layers: List<DefenseLayerInfo> = listOf(
        DefenseLayerInfo(1, "فاحص الملفات (File Scanner)", "File Scanner", true, "نشط - فحص حثيث لتنفيذات الصلاحيات", 12),
        DefenseLayerInfo(2, "فاحص التطبيقات (App Scanner)", "Application Scanner", true, "نشط - مراقبة تثبيت وحزم التطبيقات", 5),
        DefenseLayerInfo(3, "المحرك الاستكشافي (Heuristic Engine)", "Heuristic Engine", true, "نشط - تحليل الأنماط والأكواد المعقدة", 8),
        DefenseLayerInfo(4, "مؤشرات السلوك وإمكانية الوصول", "Behavior & Accessibility", true, "نشط - كشف سلوك الرسوم المتراكبة والأنشطة", 2),
        DefenseLayerInfo(5, "شبكة التصفح الآمن (Safe Browsing)", "Network Protection", true, "نشط - حجب النطاقات الخبيثة والتصيد", 7),
        DefenseLayerInfo(6, "درع الخصوصية والأذونات", "Privacy Shield", true, "نشط - حظر الوصول الخلفي للكاميرا والميكروفون", 3),
        DefenseLayerInfo(7, "استخبارات التهديدات العالمية", "Threat Intelligence", true, "محدّث - 142,580 توقيع أمني معتمد", 14),
        DefenseLayerInfo(8, "التحليل الذكي (VIP AI Security)", "AI Security Analysis", true, "نشط - تفسير وسياق أنماط الهجوم", 4),
        DefenseLayerInfo(9, "المحجر الصحي والعزل (Quarantine)", "Quarantine & Containment", true, "نشط - بيئة معزولة بالكامل بدون صلاحيات", 1),
        DefenseLayerInfo(10, "محرك الاستجابة للحوادث", "Incident Response Engine", true, "نشط - تدرج مستويات الخطر Level 1-4", 6),
        DefenseLayerInfo(11, "الحماية الذاتية (VIP Self Defense)", "App Self-Protection", true, "نشط - مراقبة سلامة التطبيق وحماية الإعدادات", 0)
    )
)

object VipSecurityEngine {

    private val _status = MutableStateFlow(ExtremeDefenseStatus())
    val status: StateFlow<ExtremeDefenseStatus> = _status.asStateFlow()

    fun toggleLayer(layerNumber: Int, enabled: Boolean) {
        val updatedLayers = _status.value.layers.map { layer ->
            if (layer.layerNumber == layerNumber) {
                layer.copy(isEnabled = enabled, statusText = if (enabled) "نشط - حماية كاملة" else "معطّل من المستخدم")
            } else {
                layer
            }
        }
        _status.value = _status.value.copy(layers = updatedLayers)
    }

    fun setExtremeDefenseActive(active: Boolean) {
        val updatedLayers = _status.value.layers.map { it.copy(isEnabled = active) }
        _status.value = _status.value.copy(
            isExtremeModeActive = active,
            layers = updatedLayers,
            protectionScore = if (active) 98 else 65
        )
    }

    fun updateMetrics(
        score: Int = _status.value.protectionScore,
        quarantined: Int = _status.value.quarantinedFilesCount,
        blockedDomains: Int = _status.value.blockedDomainsCount
    ) {
        _status.value = _status.value.copy(
            protectionScore = score,
            quarantinedFilesCount = quarantined,
            blockedDomainsCount = blockedDomains
        )
    }
}
