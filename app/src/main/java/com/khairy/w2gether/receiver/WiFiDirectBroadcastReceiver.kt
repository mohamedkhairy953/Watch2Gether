package com.khairy.w2gether.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener
import android.util.Log

@SuppressLint("MissingPermission")
class WiFiDirectBroadcastReceiver(
    private val wifiP2pManager: WifiP2pManager?,
    private val channel: WifiP2pManager.Channel,
    private val callback: WifiDirectBroadcastCallback
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("TAG", "onReceive: ${intent.action}")
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
            val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
            callback.isWifiDirectEnabled(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)

        }

        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {
            // The list of available peers has changed
            wifiP2pManager?.requestPeers(channel) {
                callback.listOfPeersChanged(it)
                // Process the list of available peers
            }
        }

        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
            if (wifiP2pManager == null) {
                return
            }
            val networkInfo =
                intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
            if (networkInfo!!.isConnected) {
                wifiP2pManager.requestConnectionInfo(channel, ConnectionInfoListener { info ->
                    callback.connectedInfo(info)
                    // Check if this device is the group owner (server) or the client
                    if (info.groupFormed && info.isGroupOwner) {
                        callback.isGroupOwner(true)
                    } else if (info.groupFormed) {
                        callback.isGroupOwner(false)
                    }
                })
            }
        }

    }
}