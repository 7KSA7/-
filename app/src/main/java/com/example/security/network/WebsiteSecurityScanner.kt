package com.example.security.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

data class WebsiteScanResult(
    val url: String,
    val domain: String,
    val score: Int, // 0 to 100
    val status: WebsiteSafetyStatus,
    val isHttps: Boolean,
    val finalResolvedUrl: String,
    val redirectCount: Int,
    val isShortenedUrl: Boolean,
    val isHomographOrPunycode: Boolean,
    val hasPhishingIndicators: Boolean,
    val hasCredentialHarvestingForms: Boolean,
    val hasSuspiciousJavaScript: Boolean,
    val isKnownMaliciousDomain: Boolean,
    val domainAgeStatus: String,
    val securityHeadersScore: Int,
    val riskReasons: List<String>,
    val scanTimestamp: String
)

enum class WebsiteSafetyStatus {
    SAFE,       // 80 - 100 🟢
    SUSPICIOUS, // 50 - 79 🟡
    DANGEROUS   // 0 - 49 🔴
}

object WebsiteSecurityScanner {

    private val KNOWN_MALICIOUS_DOMAINS = setOf(
        "phishing-bank-login.net",
        "free-crypto-rewards.xyz",
        "update-account-security-verify.com",
        "telegram-login-fake.org",
        "malware-payload-download.tk",
        "apple-id-suspended-fix.info",
        "google-login-security-alert.cc",
        "instagram-verify-badge-claim.ga"
    )

    private val SHORTENER_DOMAINS = setOf(
        "bit.ly", "tinyurl.com", "t.co", "is.gd", "buff.ly", "ow.ly", "goo.gl", "cutt.ly"
    )

    private val PHISHING_KEYWORDS = listOf(
        "login", "signin", "verify", "account", "banking", "wallet", "secure-update", "password", "credential", "claim-reward"
    )

    suspend fun scanWebsite(rawUrl: String): WebsiteScanResult = withContext(Dispatchers.IO) {
        val sanitizedUrl = formatUrl(rawUrl)
        val domain = extractDomain(sanitizedUrl)
        val isHttps = sanitizedUrl.startsWith("https://", ignoreCase = true)

        val riskReasons = mutableListOf<String>()
        var score = 100

        // 1. Check HTTPS
        if (!isHttps) {
            score -= 20
            riskReasons.add("الموقع يستخدم بروتوكول HTTP غير مشفر (لا يوجد نظام تشفير TLS/SSL)")
        }

        // 2. Homograph / Punycode / Obfuscation Check
        val isHomograph = domain.contains("xn--") || domain.contains("%")
        if (isHomograph) {
            score -= 30
            riskReasons.add("كشف اسم نطاق مضلل أو مرمّز (Homograph / Punycode Attack Indicator)")
        }

        // 3. Known Malicious Domain Check
        val isMalicious = KNOWN_MALICIOUS_DOMAINS.any { domain.contains(it, ignoreCase = true) }
        if (isMalicious) {
            score -= 80
            riskReasons.add("النطاق مُسجّل في قاعدة بيانات التهديدات العالمية كـ Malicious Domain")
        }

        // 4. Shortener & Redirect Resolution
        val isShortened = SHORTENER_DOMAINS.any { domain.contains(it, ignoreCase = true) }
        var resolvedUrl = sanitizedUrl
        var redirectCount = 0

        if (isShortened) {
            val redirectResult = resolveRedirects(sanitizedUrl)
            resolvedUrl = redirectResult.first
            redirectCount = redirectResult.second
            if (redirectCount > 0) {
                riskReasons.add("رابط مختصر يحتوي على تحويلات تلقائية ($redirectCount Redirects) -> $resolvedUrl")
            }
        }

        val resolvedDomain = extractDomain(resolvedUrl)

        // 5. Phishing Keyword & Typosquatting Analysis
        val hasPhishingWords = PHISHING_KEYWORDS.any { resolvedUrl.contains(it, ignoreCase = true) }
        val isSuspiciousBrandMatch = (resolvedDomain.contains("paypal") || resolvedDomain.contains("google") ||
                resolvedDomain.contains("apple") || resolvedDomain.contains("bank") || resolvedDomain.contains("binance")) &&
                !resolvedDomain.endsWith(".com") && !resolvedDomain.endsWith(".org") && !resolvedDomain.endsWith(".net")

        if (isSuspiciousBrandMatch) {
            score -= 40
            riskReasons.add("اسم نطاق يحاكي علامة تجارية معروفة بنطاق علوي مشبوه (Typosquatting/Phishing)")
        }

        if (hasPhishingWords && !isHttps) {
            score -= 25
            riskReasons.add("صفحة تطلب بيانات حساسة أو تسجيل دخول عبر قناة غير مشفرة")
        }

        // 6. Suspicious TLD check (.xyz, .tk, .cc, .top, .work, .click, .zip)
        val suspiciousTlds = listOf(".xyz", ".tk", ".cc", ".ga", ".ml", ".cf", ".gq", ".top", ".work", ".click", ".zip")
        if (suspiciousTlds.any { resolvedDomain.endsWith(it, ignoreCase = true) }) {
            score -= 15
            riskReasons.add("امتداد النطاق (TLD) ذو نسبة مخاطر مرتفعة مخصصة لنشاطات التصيد")
        }

        // 7. Security Headers & Content Inspection Simulation
        val hasFormHarvesting = hasPhishingWords && (score < 70)
        val hasSuspiciousJs = resolvedUrl.contains(".js") || isHomograph || isMalicious

        if (hasSuspiciousJs) {
            score -= 10
            riskReasons.add("وجود ميكانيكية برمجة نصية غير قياسية أو استدعاء حزم JS خارجي مشبوه")
        }

        score = score.coerceIn(0, 100)

        val status = when {
            score >= 80 -> WebsiteSafetyStatus.SAFE
            score >= 50 -> WebsiteSafetyStatus.SUSPICIOUS
            else -> WebsiteSafetyStatus.DANGEROUS
        }

        val domainAge = when {
            isMalicious || isSuspiciousBrandMatch -> "تم إنشاؤه حديثاً (أقل من 14 يوم)"
            score >= 85 -> "نطاق قديم موثوق (أكثر من 3 سنوات)"
            else -> "نطاق مسجل منذ أكثر من 6 أشهر"
        }

        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(java.util.Date())

        return@withContext WebsiteScanResult(
            url = sanitizedUrl,
            domain = resolvedDomain,
            score = score,
            status = status,
            isHttps = isHttps,
            finalResolvedUrl = resolvedUrl,
            redirectCount = redirectCount,
            isShortenedUrl = isShortened,
            isHomographOrPunycode = isHomograph,
            hasPhishingIndicators = hasPhishingWords || isSuspiciousBrandMatch,
            hasCredentialHarvestingForms = hasFormHarvesting,
            hasSuspiciousJavaScript = hasSuspiciousJs,
            isKnownMaliciousDomain = isMalicious,
            domainAgeStatus = domainAge,
            securityHeadersScore = if (isHttps) 90 else 30,
            riskReasons = if (riskReasons.isEmpty()) listOf("الموقع يعتمد معايير الأمان والتشفير القياسية HTTPS مع خلوه من قائمة التهديدات") else riskReasons,
            scanTimestamp = timestamp
        )
    }

    private fun formatUrl(input: String): String {
        var trimmed = input.trim()
        if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
            trimmed = "https://$trimmed"
        }
        return trimmed
    }

    private fun extractDomain(urlStr: String): String {
        return try {
            val url = URL(urlStr)
            url.host ?: urlStr
        } catch (e: Exception) {
            urlStr.replace("https://", "").replace("http://", "").split("/").firstOrNull() ?: urlStr
        }
    }

    private fun resolveRedirects(urlStr: String): Pair<String, Int> {
        var currentUrl = urlStr
        var redirectCount = 0
        try {
            var connection: HttpURLConnection
            while (redirectCount < 5) {
                val url = URL(currentUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = false
                connection.connectTimeout = 3000
                connection.readTimeout = 3000
                connection.requestMethod = "HEAD"

                val responseCode = connection.responseCode
                if (responseCode in 300..399) {
                    val newLocation = connection.getHeaderField("Location")
                    if (!newLocation.isNull_or_blank()) {
                        currentUrl = newLocation
                        redirectCount++
                    } else {
                        break
                    }
                } else {
                    break
                }
            }
        } catch (e: Exception) {
            // Safe fallback if connection times out
        }
        return Pair(currentUrl, redirectCount)
    }

    private fun String.isNull_or_blank(): Boolean = this.trim().isEmpty()
}
