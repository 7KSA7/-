package com.example.security.network

import android.content.Context
import com.example.data.database.VipDatabase
import com.example.data.database.entities.SecurityLogEntity
import com.example.security.models.NetworkConnectionInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NetworkProtectionManager {

    private val _blockedDomainsCount = MutableStateFlow(142)
    val blockedDomainsCount: StateFlow<Int> = _blockedDomainsCount.asStateFlow()

    private val _connectionLogs = MutableStateFlow<List<NetworkConnectionInfo>>(emptyList())
    val connectionLogs: StateFlow<List<NetworkConnectionInfo>> = _connectionLogs.asStateFlow()

    private val knownMaliciousDomains = listOf(
        "malware-command-control.xyz",
        "phishing-bank-login.top",
        "tracker-harvest.analytics-net.org",
        "botnet-c2-server.ru",
        "spyware-exfil.biz"
    )

    fun logConnectionAttempt(
        context: Context,
        appName: String,
        packageName: String,
        destinationHost: String,
        destinationIp: String
    ): NetworkConnectionInfo {
        val isMalicious = knownMaliciousDomains.any { destinationHost.contains(it, ignoreCase = true) } ||
                destinationHost.endsWith(".top") || destinationHost.endsWith(".xyz")

        val threatCat = if (isMalicious) "Malware C2 / Phishing" else null

        val connInfo = NetworkConnectionInfo(
            id = System.currentTimeMillis().toString(),
            packageName = packageName,
            appName = appName,
            destinationHost = destinationHost,
            destinationIp = destinationIp,
            isBlocked = isMalicious,
            threatCategory = threatCat
        )

        val updated = _connectionLogs.value.toMutableList().apply { add(0, connInfo) }
        _connectionLogs.value = updated

        if (isMalicious) {
            _blockedDomainsCount.value += 1
        }

        return connInfo
    }
}
