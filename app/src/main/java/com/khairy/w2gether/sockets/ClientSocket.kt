package com.khairy.w2gether.sockets

import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

/**
 * ...
 *
 *
 * Copyright (c) 2023 . All rights reserved.
 *
 * @author Mohamed "mohamed" Sallam.
 * @since 11/5/2023 6:51 PM
 */
class ClientSocket(hostAddress: InetAddress) : Thread() {
    val socket = Socket()
    val hostAddress: String? = hostAddress.hostAddress
    override fun run() {
        try {
            socket.connect(InetSocketAddress(hostAddress, SOCKET_PORT), SOCKET_TIME_OUT)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        super.run()
    }
}