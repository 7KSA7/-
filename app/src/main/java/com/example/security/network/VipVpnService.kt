package com.example.security.network

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log

class VipVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP) {
            stopVpn()
            return START_NOT_STICKY
        }

        if (!isRunning) {
            startVpn()
        }
        return START_STICKY
    }

    private fun startVpn() {
        try {
            val builder = Builder()
                .setSession("VIP Protection Shield")
                .addAddress("10.1.10.1", 32)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("1.1.1.1") // Cloudflare Secure DNS
                .addDnsServer("8.8.8.8")

            vpnInterface = builder.establish()
            isRunning = true
            Log.d("VipVpnService", "Network Protection VPN Interface established successfully.")
        } catch (e: Exception) {
            Log.e("VipVpnService", "Failed to establish VPN: ${e.message}")
        }
    }

    private fun stopVpn() {
        try {
            vpnInterface?.close()
            vpnInterface = null
            isRunning = false
            stopSelf()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.example.action.START_VPN"
        const val ACTION_STOP = "com.example.action.STOP_VPN"
    }
}
