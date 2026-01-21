package ro.pub.cs.systems.eim.practicaltest02v6.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ro.pub.cs.systems.eim.practicaltest02v6.R
import ro.pub.cs.systems.eim.practicaltest02v6.general.Constants
import ro.pub.cs.systems.eim.practicaltest02v6.network.ClientThread
import ro.pub.cs.systems.eim.practicaltest02v6.network.ServerThread
import java.time.LocalDateTime

class PracticalTest02v6MainActivity : AppCompatActivity() {

    private lateinit var serverPortEditText: EditText
    private lateinit var clientAddressEditText: EditText
    private lateinit var clientPortEditText: EditText
    // DE SCHIMBAT
    private lateinit var prefixEditText: EditText
    private lateinit var informationTextView: TextView

    private var serverThread: ServerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked")
        setContentView(R.layout.activity_practical_test02v6_main)

        serverPortEditText = findViewById(R.id.server_port_edit_text)
        clientAddressEditText = findViewById(R.id.client_address_edit_text)
        clientPortEditText = findViewById(R.id.client_port_edit_text)
        // DE SCHIMBAT
        prefixEditText = findViewById(R.id.currency_edit_text)
        informationTextView = findViewById(R.id.info_text_view)

        findViewById<Button>(R.id.connect_button).setOnClickListener {
            val serverPort = serverPortEditText.text.toString()
            if (serverPort.isEmpty()) {
                Toast.makeText(this, "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val thread = ServerThread(serverPort.toInt())
            if (thread.serverSocket == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!")
                return@setOnClickListener
            }

            serverThread = thread
            thread.start()
        }

        // DE SCHIMBAT
        findViewById<Button>(R.id.get_info_button).setOnClickListener {
            val clientAddress = clientAddressEditText.text.toString()
            val clientPort = clientPortEditText.text.toString()

            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(this, "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            if (serverThread?.isAlive != true) {
//                Toast.makeText(this, "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            // DE SCHIMBAT
            val prefix = prefixEditText.text.toString()

            if (prefix.isEmpty()) {
                Toast.makeText(
                    this,
                    "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            informationTextView.text = Constants.EMPTY_STRING

            // DE SCHIMBAT
            ClientThread(clientAddress, clientPort.toInt(), prefix, informationTextView).start()
        }
    }

    override fun onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked")
        serverThread?.stopThread()
        super.onDestroy()
    }
}
