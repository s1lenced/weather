package com.inc.silence.weather.extension

import com.inc.silence.weather.presentation.view.ForecastView
import java.text.SimpleDateFormat
import java.util.*

fun String.Companion.empty() = ""

fun Double.toDegree() = this.toInt().toString() + 0x00B0.toChar()


fun String.toDayOfWeek(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    val date = formatter.parse(this)
    formatter.applyPattern("EEEE")
    return formatter.format(date)
}

fun String.hour(is24: Boolean): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    val date = formatter.parse(this)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return if (is24) calendar.get(Calendar.HOUR_OF_DAY).toString() else calendar.get(Calendar.HOUR).toString()
}

fun String.amOrPm(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    val date = formatter.parse(this)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return if (calendar.get(Calendar.AM_PM) == 0) "AM" else "PM"
}

fun List<ForecastView>.sortByDate(): MutableList<MutableList<ForecastView>> {
    val mainList: MutableList<MutableList<ForecastView>> = mutableListOf()
    var innerList: MutableList<ForecastView> = mutableListOf()

    for ((index, forecast) in this.withIndex()) {
        val previous = if (index - 1 < 0) index else index - 1

        when {
            this[index].date.toDayOfWeek() == this[previous].date.toDayOfWeek() -> {
                innerList.add(forecast)
                if (this[index] == last()) mainList.add(innerList)
            }
            else -> {
                mainList.add(innerList)
                innerList = mutableListOf()
                innerList.add(forecast)
            }
        }
    }

    return mainList
}

fun List<ForecastView>.weatherByHour(): ForecastView {
    this.forEach { if (it.date.hour(true) == "15") return it }
    return this.first()
}