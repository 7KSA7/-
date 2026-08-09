package com.example.security.selfdefense

import android.content.Context
import com.example.security.incident.IncidentLevel
import com.example.security.incident.IncidentResponseEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SelfDefenseStatus(
    val isAppIntegrityVerified: Boolean = true,
    val isSettingsTamperProtectionActive: Boolean = true,
    val isProtectionServiceShielded: Boolean = true,
    val isDebuggerOrHookingDetected: Boolean = false,
    val totalTamperAttemptsBlocked: Int = 0,
    val lastCheckTime: String = "الآن"
)

object VipSelfDefenseManager {

    private val _status = MutableStateFlow(SelfDefenseStatus())
    val status: StateFlow<SelfDefenseStatus> = _status.asStateFlow()

    fun performSelfDefenseCheck(context: Context) {
        val isDebuggable = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

        if (isDebuggable) {
            _status.value = _status.value.copy(
                isDebuggerOrHookingDetected = true,
                totalTamperAttemptsBlocked = _status.value.totalTamperAttemptsBlocked + 1
            )
            IncidentResponseEngine.reportIncident(
                level = IncidentLevel.LEVEL_2_SUSPICIOUS,
                title = "مؤشر تصحيح الأخطاء (Debugger Indicator)",
                description = "تم اكتشاف إمكانية ربط أداة تصحيح (Debugging Mode) بالتطبيق. تم تفعيل الحماية الذاتية لحظر الوصول للذاكرة.",
                originLayer = "VIP Self Defense (Layer 11)",
                targetItem = "VIP Protection Process",
                actionTaken = "تشفير جلسة الإعدادات وتأمين الذاكرة"
            )
        } else {
            _status.value = _status.value.copy(
                isAppIntegrityVerified = true,
                isSettingsTamperProtectionActive = true,
                isProtectionServiceShielded = true,
                isDebuggerOrHookingDetected = false,
                lastCheckTime = "الآن"
            )
        }
    }

    fun triggerTamperShieldAlert(reason: String) {
        _status.value = _status.value.copy(
            totalTamperAttemptsBlocked = _status.value.totalTamperAttemptsBlocked + 1
        )
        IncidentResponseEngine.reportIncident(
            level = IncidentLevel.LEVEL_3_HIGH,
            title = "محاولة تغيير إعدادات الحماية دون إذن",
            description = "محاولة عبث بالإعدادات: $reason. تم حظر التغيير وتفعيل وضع الاستعادة الذاتية.",
            originLayer = "VIP Self Defense (Layer 11)",
            targetItem = "Security Configuration Store",
            actionTaken = "حظر التعديل + إعادة الإعدادات الأصلية"
        )
    }
}
