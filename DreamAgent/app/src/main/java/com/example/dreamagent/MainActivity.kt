package com.example.dreamagent

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dreamagent.ui.theme.DreamAgentTheme
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private lateinit var mSocket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Socket.IO connection
        try {
            // Use your machine's local IP address for real devices or 10.0.2.2 for emulator
            mSocket = IO.socket("http://10.0.2.2:8080")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Listen for new messages
        mSocket.on("new message", onNewMessage)
        mSocket.connect()

        setContent {
            DreamAgentTheme {
                MessageInputScreen(
                    onSendMessage = { message ->
                        attemptSend(message)
                    }
                )
            }
        }
    }

    // Function to send a message to the server
    private fun attemptSend(message: String) {
        if (message.isNotEmpty()) {
            mSocket.emit("new message", message)
            Log.d("SocketIO", "Message sent: $message")
        } else {
            Log.d("SocketIO", "Message is empty, not sent.")
        }
    }

    // Listener for incoming 'new message' events
    private val onNewMessage = Emitter.Listener { args ->
        runOnUiThread {
            try {
                val data = args[0] as JSONObject
                val username = data.getString("username")
                val message = data.getString("message")
                // Log the message or update the UI
                Log.d("SocketIO", "$username: $message")
            } catch (e: JSONException) {
                Log.e("SocketIO", "Error parsing JSON: ${e.message}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Properly disconnect the socket and remove the listener
        mSocket.disconnect()
        mSocket.off("new message", onNewMessage)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputScreen(onSendMessage: (String) -> Unit) {
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Text input for typing the message
        TextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Type a message") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Send button
        Button(
            onClick = {
                if (message.isNotEmpty()) {
                    onSendMessage(message)
                    message = "" // Clear the input field after sending
                } else {
                    Log.d("SocketIO", "Message is empty, cannot send.")
                }
            },

        ) {
            Text(text = "Send")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageInputScreenPreview() {
    DreamAgentTheme {
        MessageInputScreen(onSendMessage = {})
    }
}
