package ro.pub.cs.systems.eim.practicaltest02v6.network

import kotlin.text.get

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ro.pub.cs.systems.eim.practicaltest02v6.general.Constants
import ro.pub.cs.systems.eim.practicaltest02v6.general.Utilities.getReader
import ro.pub.cs.systems.eim.practicaltest02v6.general.Utilities.getWriter
import ro.pub.cs.systems.eim.practicaltest02v6.general.Utilities.timeUSD
import ro.pub.cs.systems.eim.practicaltest02v6.general.Utilities.timeEUR
import ro.pub.cs.systems.eim.practicaltest02v6.model.Information
import java.io.IOException
import java.net.Socket
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds

class CommunicationThread(
    private val serverThread: ServerThread,
    private val socket: Socket
) : Thread() {

    override fun run() {
        try {
            socket.use { s ->
                val bufferedReader = getReader(s)
                val printWriter = getWriter(s)

                Log.i(
                    Constants.TAG,
                    "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type)!"
                )

                // DE SCHIMBAT
                val currency = bufferedReader.readLine()

                // DE SCHIMBAT
                if (currency.isNullOrEmpty()) {
                    Log.e(
                        Constants.TAG,
                        "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type)!"
                    )
                    return
                }

                // DE SCHIMBAT
                val cached = serverThread.data[currency]
                val currentTime = LocalDateTime.now()


                if ((currency == "USD" && (currentTime > timeUSD.plusSeconds(10)))
                    || ((currency == "EUR" && (currentTime > timeEUR.plusSeconds(10))))
                ) {
                    val information = cached ?: run {
                        Log.i(
                            Constants.TAG,
                            "[COMMUNICATION THREAD] Getting the information from the webservice..."
                        )

                        val httpClient = OkHttpClient()
                        // DE SCHIMBAT
                        val url =
                            "${Constants.WEB_SERVICE_ADDRESS}$currency"

                        // HTTP GET
                        val request = Request.Builder()
                            .url(url)
                            .build()

                        val httpResponse = httpClient.newCall(request).execute().use { response ->
                            if (!response.isSuccessful || response.body == null) {
                                // ERROR
                                return
                            }
                            response.body!!.string()
                        }

                        val content = JSONObject(httpResponse)

                        val myDataObject = content.getJSONObject("Data")
                        val BTC = myDataObject.getJSONObject("BTC-$currency")

                        Log.i(Constants.TAG, BTC.toString())

                        val condition = StringBuilder()
                        condition.append(BTC.getString("VALUE"))
                        val value: String
                        val myTime: LocalDateTime
                        if (currency == "USD") {
                            value = condition.toString()
                            myTime = timeUSD
                            timeUSD = LocalDateTime.now()
                        } else {
                            value = condition.toString()
                            myTime = timeEUR
                            timeEUR = LocalDateTime.now()
                        }

                        val info = Information(
                            value = value,
                            time = myTime
                        )

                        // DE SCHIMBAT
                        serverThread.setData(currency, info)
                        // returning
                        info
                    }

                    // DE SCHIMBAT
                    val result = information.value

                    printWriter.println(result)
                    printWriter.flush()
                } else {
                    Log.i(
                        Constants.TAG,
                        "[COMMUNICATION THREAD] Getting the information from cache..."
                    )

                    val result = serverThread.data[currency]!!.value.toString()

                    printWriter.println(result)
                    printWriter.flush()
                }

                // DE SCHIMBAT

//                    for (i in 0 until informationArray.length()) {
//                        // DE SCHIMBAT
//                        val informationObj = informationArray.getString(i)
//                        condition.append(informationObj)
//
//                        if (i < informationArray.length() - 1) condition.append(",")
//                    }


            }
        } catch (e: IOException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: ${e.message}")
        } catch (e: JSONException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: ${e.message}")
        }
    }
}
