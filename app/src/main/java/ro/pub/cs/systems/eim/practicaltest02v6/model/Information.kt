package ro.pub.cs.systems.eim.practicaltest02v6.model

import java.time.LocalDateTime

data class Information(
    val value: String?,
    val time: LocalDateTime?
) {
//    override fun toString(): String {
//        return "Information(temperature=$temperature, windSpeed=$windSpeed, condition=$condition, pressure=$pressure, humidity=$humidity)"
//    }
}
