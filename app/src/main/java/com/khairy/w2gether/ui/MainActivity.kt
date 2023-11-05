package com.khairy.w2gether.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khairy.w2gether.receiver.WiFiDirectBroadcastReceiver
import com.khairy.w2gether.receiver.WifiDirectBroadcastCallback


@OptIn(ExperimentalMaterial3Api::class)
val PERMISSIONS = arrayOf(
    Manifest.permission.CHANGE_WIFI_STATE,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_WIFI_STATE,
    Manifest.permission.NEARBY_WIFI_DEVICES
)

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity(), WifiDirectBroadcastCallback {
    private lateinit var multiplePermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var multiplePermissionsContract: ActivityResultContracts.RequestMultiplePermissions
    private var wifiP2pManager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private val intentFilter = IntentFilter()
    private var receiver: BroadcastReceiver? = null
    private val discoveredPeers = mutableStateListOf<WifiP2pDevice>()
    private val connectedDevice = mutableStateOf(null)
    private val connectionListener = object : ConnectionInfoListener {
        override fun onConnectionInfoAvailable(p0: WifiP2pInfo?) {

        }

    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        multiplePermissionsContract = ActivityResultContracts.RequestMultiplePermissions()
        multiplePermissionLauncher =
            registerForActivityResult(multiplePermissionsContract) { isGranted ->

            }
        setContent {
            wifiP2pManager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
            channel = wifiP2pManager!!.initialize(this, Looper.getMainLooper(), null)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            multiplePermissionLauncher.launch(PERMISSIONS)

            Scaffold() {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    DiscoverPeersButton()
                    Spacer(modifier = Modifier.height(16.dp))
                    DiscoveredPeersList()
                }
            }
        }
    }

    @Composable
    private fun DiscoveredPeersList() {
        discoveredPeers.forEach {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .clickable {
                        connect(it)
                    },
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Device Name ->  ${it.deviceName}")
                Text(text = "Device Address ->  ${it.deviceAddress}")
                Text(text = "Primary Device Type -> ${it.primaryDeviceType}")
                Text(text = "Secondary Device Type -> ${it.secondaryDeviceType}")
                Text(text = "Status -> ${it.status}")
            }
        }
    }

    @Composable
    private fun DiscoverPeersButton() {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .align(Alignment.Center),
                onClick = { discoverPeers() }) {
                Box(modifier = Modifier) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Discover Peers"
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        channel?.let { chnl ->
            Log.d("TAG", "onResume: register ")
            receiver = WiFiDirectBroadcastReceiver(wifiP2pManager, chnl, this)
            registerReceiver(receiver, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        if (receiver != null)
            unregisterReceiver(receiver)
    }

    private fun discoverPeers() {
        wifiP2pManager!!.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("Wi-Fi Direct", "Discovery Initiated")
            }

            override fun onFailure(reasonCode: Int) {
                Log.d("Wi-Fi Direct", "Discovery Failed: $reasonCode")
            }
        })
        wifiP2pManager!!.discoverServices(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("Wi-Fi Direct", "discoverServices Initiated")
            }

            override fun onFailure(p0: Int) {
                Log.d("Wi-Fi Direct", "discoverServices Initiated")
            }

        })
    }

    fun connect(device: WifiP2pDevice) {
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        wifiP2pManager!!.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // Connection request successful
                Log.d("TAG", "onSuccess: ")
            }

            override fun onFailure(reason: Int) {
                Log.d("TAG", "onFailure: ")
            }
        })
    }

    override fun isWifiDirectEnabled(isEnabled: Boolean) {
        if (!isEnabled)
            Toast.makeText(this, "Wifi Direct is not enabled", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "Wifi Direct is enabled", Toast.LENGTH_SHORT).show()

    }

    override fun isGroupOwner(isOwner: Boolean) {
        if (isOwner)
            Toast.makeText(this, "Device is owner", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "Device is client", Toast.LENGTH_SHORT).show()

    }

    override fun connectedInfo(info: WifiP2pInfo) {

    }

    override fun listOfPeersChanged(list: WifiP2pDeviceList) {
        discoveredPeers.clear()
        discoveredPeers.addAll(list.deviceList)
    }
}