package com.example.weatherapp.db

import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun getCurrentWeather(): Flow<List<WeatherResponse>>
}