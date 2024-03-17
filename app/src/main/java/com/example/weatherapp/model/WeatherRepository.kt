package com.example.weatherapp.model
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
     suspend fun getWeatherForecast(
          latitude: Double,
          longitude: Double,
          language: String,
          units: String?,
     ): Flow<WeatherResponse>

     fun getAllWeather(): Flow<List<FavoriteWeather>>
     suspend fun insertWeather(favWeather: FavoriteWeather)
     suspend fun deleteWeather(favWeather: FavoriteWeather)

     fun getAllAlerts(): Flow<List<AlertPojo>>
     suspend fun  insertAlert(alert: AlertPojo)
     suspend  fun deleteAlert(alert: AlertPojo)

     fun getAlertWithId(id: String): AlertPojo

}