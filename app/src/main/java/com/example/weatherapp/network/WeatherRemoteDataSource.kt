package com.example.weatherapp.network

import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    suspend fun getOneCallResponse(
        lat: Double?,
        lon: Double?,
    ): WeatherResponse
}