package ro.pub.cs.systems.eim.practicaltest02v6.general

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

object Utilities {
    fun getReader(socket: Socket): BufferedReader {
        return BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    fun getWriter(socket: Socket): PrintWriter {
        return PrintWriter(socket.getOutputStream(), true)
    }
}