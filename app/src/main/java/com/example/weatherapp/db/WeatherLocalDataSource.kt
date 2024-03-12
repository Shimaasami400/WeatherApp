package com.example.weatherapp.db

import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun getAllWeather(): Flow<List<FavoriteWeather>>
    suspend fun insertWeather(productsItem: FavoriteWeather)
    suspend fun deleteWeather(productsItem: FavoriteWeather)
}