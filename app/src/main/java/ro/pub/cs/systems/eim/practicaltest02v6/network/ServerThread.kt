package ro.pub.cs.systems.eim.practicaltest02v6.network

import android.util.Log
import ro.pub.cs.systems.eim.practicaltest02v6.general.Constants
import ro.pub.cs.systems.eim.practicaltest02v6.model.Information
import java.io.IOException
import java.net.ServerSocket

class ServerThread(port: Int) : Thread() {

    var serverSocket: ServerSocket? = null

//    data class Key(val city: String, val units: String, val lang: String)
//    val map = HashMap<Key, WeatherForecastInformation>()
//    map[Key("Bucharest", "metric", "ro")] = info
//    val x = map[Key("Bucharest", "metric", "ro")]

    // DE SCHIMBAT STRUCTURA CHEIE
    val data = HashMap<String, Information?>()

    init {
        try {
            serverSocket = ServerSocket(port)
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: ${ioException.message}")
        }
    }

    @Synchronized
    fun setData(prefix: String, information: Information?) {
        // DE SCHIMBAT
        data[prefix] = information
    }

    override fun run() {
        val server = serverSocket ?: return

        try {
            while (!isInterrupted) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...")
                val socket = server.accept()
                Log.i(
                    Constants.TAG,
                    "[SERVER THREAD] A connection request was received from ${socket.inetAddress}:${socket.localPort}"
                )
                CommunicationThread(this, socket).start()
            }
        } catch (ioException: IOException) {
            // daca thread-ul e oprit intenționat, accept() poate arunca IOException cand se inchide socket-ul
            if (!isInterrupted) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: ${ioException.message}")
            }
        }
    }

    fun stopThread() {
        interrupt()
        try {
            serverSocket?.close()
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: ${ioException.message}")
        } finally {
            serverSocket = null
        }
    }
}
