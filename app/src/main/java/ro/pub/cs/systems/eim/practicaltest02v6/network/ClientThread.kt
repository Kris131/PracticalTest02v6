package ro.pub.cs.systems.eim.practicaltest02v6.network

import android.util.Log
import android.widget.TextView
import ro.pub.cs.systems.eim.practicaltest02v6.general.Constants
import ro.pub.cs.systems.eim.practicaltest02v6.general.Utilities
import java.io.IOException
import java.net.Socket

class ClientThread(
    private val address: String,
    private val port: Int,
    // DE SCHIMBAT
    private val currency: String,
    private val InformationTextView: TextView
) : Thread() {

    override fun run() {
        try {
            Socket(address, port).use { socket ->
                val bufferedReader = Utilities.getReader(socket)
                val printWriter = Utilities.getWriter(socket)

                printWriter.println(currency)
                printWriter.flush()

                while (true) {
                    val line = bufferedReader.readLine() ?: break
//                    val newLine = line.replace(",", ",\n")
//                    Log.d("test", newLine)
                    InformationTextView.post { InformationTextView.text = line }
                }
            }
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: ${ioException.message}")
        }
    }
}
