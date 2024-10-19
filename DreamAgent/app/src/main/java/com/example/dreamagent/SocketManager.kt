package com.example.dreamagent

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketManager {

    private lateinit var mSocket: Socket

    // Method to initialize or get the existing socket
    fun getSocket(): Socket {
        if (!::mSocket.isInitialized || !mSocket.connected()) {
            try {
                val options = IO.Options().apply {
                    reconnection = false // Disable auto-reconnection
                }
                mSocket = IO.socket("http://10.0.2.2:3000", options)
                mSocket.connect()

                // Add the event listeners
                addSocketListeners()

            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        } else {
            Log.d("SocketIO", "Socket is already connected")
        }
        return mSocket
    }

    // Add listeners to the socket for connection, disconnection, and error handling
    private fun addSocketListeners() {
        mSocket.on(Socket.EVENT_CONNECT) {
            Log.d("SocketIO", "Connected to server")
        }

        mSocket.on(Socket.EVENT_DISCONNECT) {
            Log.d("SocketIO", "Disconnected from server")
        }

        mSocket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.e("SocketIO", "Connection error: ${args[0]}")
        }

        // Listen for 'simple_test' event responses from the server
        mSocket.on("simple_test_response") { args ->
            val response = args[0] as String
            Log.d("SocketIO", "Received response for 'simple_test': $response")
        }
    }

    // Method to disconnect the socket
    fun disconnect() {
        if (::mSocket.isInitialized && mSocket.connected()) {
            mSocket.disconnect()
            Log.d("SocketIO", "Socket disconnected")
        }
    }

    // Method to reconnect the socket manually
    fun reconnect() {
        if (!::mSocket.isInitialized || !mSocket.connected()) {
            mSocket.connect()
            Log.d("SocketIO", "Reconnecting to server...")
        } else {
            Log.d("SocketIO", "Already connected")
        }
    }
}
