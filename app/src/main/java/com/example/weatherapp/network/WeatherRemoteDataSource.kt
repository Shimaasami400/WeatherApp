package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherResponse

interface WeatherRemoteDataSource {
    suspend fun getOneCallResponse(
        lat: Double?,
        lon: Double?,
    ): WeatherResponse
}