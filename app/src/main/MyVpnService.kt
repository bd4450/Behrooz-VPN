
package com.behrooz.vpn

import android.content.Intent
import android.net.VpnService
import android.util.Log

class MyVpnService : VpnService() {

    companion object {
        const val ACTION_START = "com.behrooz.vpn.START"
        const val ACTION_STOP = "com.behrooz.vpn.STOP"
        private const val TAG = "BehroozVPN"
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {
            ACTION_START -> {
                Log.i(TAG, "VPN start requested")
                // Connection engine will be added next.
            }

            ACTION_STOP -> {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onRevoke() {
        super.onRevoke()
        stopSelf()
    }

    override fun onDestroy() {
        Log.i(TAG, "VPN service stopped")
        super.onDestroy()
    }
}
