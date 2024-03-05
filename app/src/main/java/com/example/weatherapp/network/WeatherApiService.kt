package com.example.weatherapp.network


import com.example.weatherapp.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("onecall")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double?,
        @Query("lon") longitude: Double?,
        @Query("appid") apiKey: String = "adb5f9b065df229b523bbc9ef5b27f49"
    ): WeatherResponse
}