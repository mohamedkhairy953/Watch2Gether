package com.khairy.w2gether.sockets

import android.util.Log
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

/**
 * ...
 *
 *
 * Copyright (c) 2023 . All rights reserved.
 *
 * @author Mohamed "mohamed" Sallam.
 * @since 11/5/2023 6:48 PM
 */
class SeverClass : Thread() {
    val serverSocket = ServerSocket(SOCKET_PORT)
    var socket = Socket()

    override fun run() {
        try {
            socket = serverSocket.accept()
        } catch (e: IOException) {
            Log.d("TAG", "run:Server Socket $e ")
            e.printStackTrace()
        }
        super.run()
    }
}