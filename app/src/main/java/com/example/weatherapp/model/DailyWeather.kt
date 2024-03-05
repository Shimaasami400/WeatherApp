package com.example.weatherapp.model


import java.text.SimpleDateFormat
import java.util.*

data class DailyWeather(
    val dayOfWeek: String,
    val date: String,
    val maxTemperature: Double,
    val minTemperature: Double
)

fun convertToDailyWeather(dailyItems: List<DailyItem>): List<DailyWeather> {
    val dailyWeatherList = mutableListOf<DailyWeather>()

    for (dailyItem in dailyItems) {
        val dayOfWeek = getDayOfWeek(dailyItem.dt)
        val date = getDate(dailyItem.dt)
        val maxTemperature = dailyItem.temp.max
        val minTemperature = dailyItem.temp.min

        val dailyWeather = DailyWeather(dayOfWeek, date, maxTemperature, minTemperature)
        dailyWeatherList.add(dailyWeather)
    }

    return dailyWeatherList
}

private fun getDayOfWeek(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp * 1000 // Convert seconds to milliseconds
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
    return dayOfWeek ?: ""
}

private fun getDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = Date(timestamp * 1000) // Convert seconds to milliseconds
    return dateFormat.format(date)
}