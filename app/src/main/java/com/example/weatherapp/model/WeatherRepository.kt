package com.example.weatherapp.model
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
     suspend fun getWeatherForecast(
          latitude: Double,
          longitude: Double,
     ): Flow<WeatherResponse>

      //fun getCurrentWeather(): Flow<List<WeatherResponse>>
}