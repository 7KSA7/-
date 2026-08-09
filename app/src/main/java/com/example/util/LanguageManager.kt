package com.example.util

import java.util.Locale

object LanguageManager {

    enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
        ARABIC("ar", "العربية", "🇸🇦"),
        ENGLISH("en", "English", "🇺🇸"),
        FRENCH("fr", "Français", "🇫🇷")
    }

    fun isRtl(langCode: String): Boolean {
        return langCode == "ar"
    }

    fun getTranslation(key: String, lang: String): String {
        val translations: Map<String, Map<String, String>> = mapOf(
            "app_title" to mapOf("ar" to "الحماية المطلقة", "en" to "VIP PROTECTION", "fr" to "PROTECTION VIP"),
            "motto" to mapOf("ar" to "PROTECT. DETECT. DEFEND.", "en" to "PROTECT. DETECT. DEFEND.", "fr" to "PROTÉGER. DÉTECTER. DÉFENDRE."),
            "device_secure" to mapOf("ar" to "جهازك محمي", "en" to "DEVICE SECURE", "fr" to "APPAREIL SÉCURISÉ"),
            "full_scan" to mapOf("ar" to "🔎 فحص كل شيء", "en" to "🔎 FULL DEVICE SCAN", "fr" to "🔎 ANALYSE COMPLÈTE"),
            "quick_scan" to mapOf("ar" to "⚡ فحص سريع", "en" to "⚡ QUICK SCAN", "fr" to "⚡ ANALYSE RAPIDE"),
            "absolute_protection" to mapOf("ar" to "الحماية المطلقة", "en" to "Absolute Protection", "fr" to "Protection Absolue"),
            "quarantine" to mapOf("ar" to "☣️ المحجر الصحي", "en" to "☣️ QUARANTINE", "fr" to "☣️ QUARANTAINE"),
            "vault" to mapOf("ar" to "🔐 الخزنة المشفرة", "en" to "🔐 SECURE VAULT", "fr" to "🔐 COFFRE SÉCURISÉ"),
            "privacy_shield" to mapOf("ar" to "🧅 درع الخصوصية", "en" to "🧅 PRIVACY SHIELD", "fr" to "🧅 BOUCLIER CONFIDENTIALITÉ"),
            "network_protection" to mapOf("ar" to "🌐 حماية الشبكة", "en" to "🌐 NETWORK PROTECTION", "fr" to "🌐 PROTECTION RÉSEAU"),
            "python_scanner" to mapOf("ar" to "🐍 فاحص بايثون", "en" to "🐍 PYTHON SCANNER", "fr" to "🐍 SCANNER PYTHON"),
            "anti_spyware" to mapOf("ar" to "🕵️ عدم التجسس", "en" to "🕵️ ANTI-SPYWARE", "fr" to "🕵️ ANTI-ESPION"),
            "ai_assistant" to mapOf("ar" to "🧠 مساعد VIP AI", "en" to "🧠 VIP AI ASSISTANT", "fr" to "🧠 ASSISTANT VIP AI"),
            "developers" to mapOf("ar" to "المطورون VO + QASSAM", "en" to "Developers VO + QASSAM", "fr" to "Développeurs VO + QASSAM"),
            "emergency_lockdown" to mapOf("ar" to "🚨 الإغلاق الطارئ", "en" to "🚨 EMERGENCY LOCKDOWN", "fr" to "🚨 VERROUILLAGE D'URGENCE")
        )

        val itemMap = translations[key] ?: return key
        return itemMap[lang] ?: itemMap["en"] ?: key
    }
}
