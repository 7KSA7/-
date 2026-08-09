package com.example.security.ai

import com.example.BuildConfig
import com.example.security.network.WebsiteScanResult
import com.example.security.network.WebsiteSafetyStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object VipAiAssistant {

    private const val MODEL = "gemini-3.5-flash"
    private const val ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeSecurityQuery(prompt: String, contextInfo: String = ""): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateOfflineAiResponse(prompt, contextInfo)
        }

        val systemPromptText = """
            You are VIP AI (مساعد VIP الأمني الذكي), an expert cybersecurity, malware analysis, and URL Threat Intelligence engine for the VIP PROTECTION Android application developed by VO + QASSAM.
            Provide detailed, professional, highly informative security answers in Arabic (or language requested).
            Explain malware threats, phishing URL traps, domain reputation, permission analysis, python vulnerabilities, and defense-in-depth security best practices clearly.
        """.trimIndent()

        val fullPrompt = if (contextInfo.isNotBlank()) {
            "السياق الأمني الحالي:\n$contextInfo\n\nسؤال المستخدم:\n$prompt"
        } else {
            prompt
        }

        try {
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply { put("text", fullPrompt) })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val systemInstructionObj = JSONObject().apply {
                    val sysPartsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemPromptText) })
                    }
                    put("parts", sysPartsArray)
                }
                put("systemInstruction", systemInstructionObj)
            }

            val req = Request.Builder()
                .url("$ENDPOINT?key=$apiKey")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(req).execute()
            val respBody = response.body?.string() ?: ""

            if (response.isSuccessful && respBody.isNotBlank()) {
                val jsonResp = JSONObject(respBody)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val cand = candidates.getJSONObject(0)
                    val content = cand.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val replyText = parts.getJSONObject(0).optString("text", "")
                        if (replyText.isNotBlank()) {
                            return@withContext replyText
                        }
                    }
                }
            }
            generateOfflineAiResponse(prompt, contextInfo)
        } catch (e: Exception) {
            generateOfflineAiResponse(prompt, contextInfo)
        }
    }

    fun generateWebsiteScanAiExplanation(scan: WebsiteScanResult): String {
        val statusText = when(scan.status) {
            WebsiteSafetyStatus.SAFE -> "آمن وموثوق 🟢"
            WebsiteSafetyStatus.SUSPICIOUS -> "مشبوه يحتاج حذر 🟡"
            WebsiteSafetyStatus.DANGEROUS -> "خطير جداً / تصيد أو برمجيات خبيثة 🔴"
        }

        val reasonsFormatted = scan.riskReasons.joinToString("\n• ") { it }

        return """
            🌐 **تحليل VIP AI لرابط الموقع (${scan.domain}):**
            
            • **درجة الأمان:** ${scan.score} / 100 ($statusText)
            • **التشفير:** ${if (scan.isHttps) "HTTPS مشفر مع شهادة TLS" else "HTTP غير مشفر (مخاطرة)"}
            • **عمر النطاق والتنقل:** ${scan.domainAgeStatus} (${scan.redirectCount} تحويلات تلقائية)
            
            ⚠️ **تحليل مؤشرات الخطر:**
            • $reasonsFormatted
            
            🛡️ **توصية VIP AI:**
            ${if (scan.score < 50) "ننصح بعدم زيارة هذا الموقع مطلقاً، وعدم إدخال أي كلمات مرور أو بيانات شخصية أو حزام بنكية فيه." else if (scan.score < 80) "يرجى توخي الحذر عند تصفح الصفحة وتجنب تحميل أي ملفات تنفيذية." else "الموقع يتبع المعايير الأمانية العادية ويمكن تصفحه بأمان."}
        """.trimIndent()
    }

    private fun generateOfflineAiResponse(prompt: String, contextInfo: String): String {
        val p = prompt.lowercase()
        return when {
            p.contains("موقع") || p.contains("رابط") || p.contains("http") || p.contains("url") -> {
                "🤖 **VIP AI - تحليل أمان المواقع والروابط:**\n\nيعتمد نظام **Website Security Scanner** على فحص متكامل دون فتح الرابط مباشرة على جهازك:\n1️⃣ **تشفير TLS/SSL:** التأكد من وجود بروتوكول HTTPS وحماية البيانات أثناء العبور.\n2️⃣ **كشف التصيد الاحتيالي (Phishing):** البحث عن النطاقات المضللة (Punycode/Homograph) والصفحات المزيفة التي تجمع بيانات الدخول.\n3️⃣ **فحص الروابط المختصرة:** تحويل روابط bit.ly و tinyurl لمعرفة الوجهة النهائية بأمان.\n4️⃣ **قاعدة بيانات التهديدات:** مطابقة النطاق مع قوائم القوائم السوداء الخبيثة عالمياً."
            }
            p.contains("ليش") || p.contains("خطير") || p.contains("سبب") -> {
                "🤖 **VIP AI - تحليل التهديدات والأجهزة:**\n\nيتم تصنيف العنصر كعنصر عالي الخطورة عند وجود عدة مؤشرات متزامنة:\n1️⃣ **الصلاحيات الحساسة:** مثل إمكانية الوصول إلى خدمة إمكانية الوصول (Accessibility) أو إذن تثبيت الحزم بدون إذن.\n2️⃣ **نمط الأكواد:** مثل استدعاء أوامر النظام Shell أو استخدام الاتصالات الشبكية غير المشفرة C2.\n3️⃣ **التحليل الرقمي:** ارتفاع نسبة التشفير والتشويش (High Entropy) داخل الملف لمنع الفحص الساكن.\n\n🛡️ يُنصح بعزل الملف داخل المحجر الصحي (Quarantine) لمنع تشغيله."
            }
            p.contains("صلاحيات") || p.contains("أذونات") -> {
                "🤖 **VIP AI - شرح الأذونات وصلاحيات التطبيق:**\n\nالصلاحيات الأكثر خطورة على بيئة Android:\n• **Accessibility Service:** تسمح للتطبيق بقراءة الشاشة ومحاكاة النقر التلقائي.\n• **System Alert Window:** تسمح برسم واجهات وهمية فوق التطبيقات الأخرى (Overlay Attack).\n• **SMS / Call Logs:** تسمح بقراءة أرقام التحقق والرسائل البنكية.\n\nتأكد دائمًا من مراجعة قسم Anti-Spyware لمعرفة تقييم الخصوصية لكل تطبيق."
            }
            p.contains("بايثون") || p.contains("python") -> {
                "🤖 **VIP AI - تحليل سكربتات بايثون:**\n\nيقوم نظام Python Security Scanner بفحص المكتبات المستوردة المتقدمة مثل `subprocess` و `socket` و `requests` والتأكد من عدم وجود روابط مخفية لـ Webhooks أو أساليب سرقة الـ Tokens والجلسات.\n\nإذا ظهر التهديد كـ Dangerous يُفضل مراجعة السكربت أو عزله فوراً."
            }
            else -> {
                "🤖 **VIP AI - مساعد الحماية والأمن الرقمي:**\n\nأهلاً بك في نظام VIP AI. أنا هنا لمساعدتك في فهم تقارير الفحص والتهديدات الأمنية، وتفسير أسباب عزل الملفات وطريقة حماية جهازك ضمن نظام Defense in Depth.\n\nتطوير: **VO + QASSAM** (@VO_HQQ, @l1_73).\nكيف يمكنني مساعدتك في فحص النظام الآن؟"
            }
        }
    }
}
