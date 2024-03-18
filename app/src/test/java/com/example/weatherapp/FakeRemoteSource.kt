package com.example.weatherapp

import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.WeatherRemoteDataSource

class FakeRemoteSource (private var weatherResponse: WeatherResponse):WeatherRemoteDataSource {
    override suspend fun getOneCallResponse(
        lat: Double?,
        lon: Double?,
        language: String,
        units: String?
    ): WeatherResponse {
        return weatherResponse
    }
}