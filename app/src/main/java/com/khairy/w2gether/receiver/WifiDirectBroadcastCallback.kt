package com.khairy.w2gether.receiver

import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo

/**
 * ...
 *
 *
 * Copyright (c) 2023 . All rights reserved.
 *
 * @author Mohamed "mohamed" Sallam.
 * @since 11/2/2023 4:48 PM
 */
interface WifiDirectBroadcastCallback {
    fun isWifiDirectEnabled(isEnabled: Boolean)
    fun isGroupOwner(isOwner: Boolean)
    fun connectedInfo(info: WifiP2pInfo)
    fun listOfPeersChanged(list: WifiP2pDeviceList)
}