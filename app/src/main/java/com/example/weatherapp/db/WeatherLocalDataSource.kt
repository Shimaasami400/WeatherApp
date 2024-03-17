package com.example.weatherapp.db

import com.example.weatherapp.model.AlertPojo
import com.example.weatherapp.model.FavoriteWeather
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun getAllWeather(): Flow<List<FavoriteWeather>>
    suspend fun insertWeather(favWeather: FavoriteWeather)
    suspend fun deleteWeather(favWeather: FavoriteWeather)

    fun getAllAlerts(): Flow<List<AlertPojo>>
    suspend fun  insertAlert(alert: AlertPojo)
    suspend  fun deleteAlert(alert: AlertPojo)

    fun getAlertWithId(id: String): AlertPojo
}