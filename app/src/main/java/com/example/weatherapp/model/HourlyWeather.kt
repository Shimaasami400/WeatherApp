package com.example.weatherapp.model

import java.util.Calendar

data class HourlyWeather(
    val hour: Int,
    val amPm: String,
    val temperature: Double
)

fun convertToHourlyWeather(hourlyWeather: List<HourlyItem>): List<HourlyWeather> {
    val hourlyWeatherList = mutableListOf<HourlyWeather>()
    val calendar = Calendar.getInstance()

    for (hourlyItem in hourlyWeather) {
        calendar.timeInMillis = hourlyItem.dt * 1000
        val hour = calendar.get(Calendar.HOUR)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        val temperature = hourlyItem.temp
        hourlyWeatherList.add(HourlyWeather(hour, amPm, temperature))
    }

    return hourlyWeatherList
}