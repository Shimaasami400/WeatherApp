package com.example.weatherapp.model

data class WeatherResponse(
	val current: Current,
	val timezone: String,
	val timezoneOffset: Long,
	val daily: List<DailyItem>,
	val lon: Double,
	val hourly: List<HourlyItem>,
	val lat: Double
)

data class Current(
	val sunrise: Int,
	val temp: Double,
	val visibility: Int,
	val uvi: Int,
	val pressure: Int,
	val clouds: Int,
	val feelsLike: Double,
	val dt: Long,
	val windDeg: Int,
	val dewPoint: Double,
	val sunset: Int,
	val weather: List<WeatherItem>,
	val humidity: Int,
	val windSpeed: Int
)

data class FeelsLike(
	val eve: Double,
	val night: Double,
	val day: Double,
	val morn: Double
)

data class HourlyItem(
	val temp: Double,
	val visibility: Int,
	val uvi: Double,
	val pressure: Int,
	val clouds: Int,
	val feelsLike: Double,
	val windGust: Double,
	val dt: Long,
	val pop: Double,
	val windDeg: Int,
	val dewPoint: Double,
	val weather: List<WeatherItem>,
	val humidity: Int,
	val windSpeed: Double
)

data class DailyItem(
	val moonset: Long,
	val summary: String,
	val sunrise: Long,
	val temp: Temp,
	val moonPhase: Double,
	val uvi: Double,
	val moonrise: Long,
	val pressure: Int,
	val clouds: Int,
	val feelsLike: FeelsLike,
	val windGust: Double,
	val dt: Long,
	val pop: Double,
	val windDeg: Int,
	val dewPoint: Double,
	val sunset: Long,
	val weather: List<WeatherItem>,
	val humidity: Int,
	val windSpeed: Double
)

data class WeatherItem(
	val icon: String,
	val description: String,
	val main: String,
	val id: Int
)

data class Temp(
	val min: Double,
	val max: Double,
	val eve: Double,
	val night: Double,
	val day: Double,
	val morn: Double
)

