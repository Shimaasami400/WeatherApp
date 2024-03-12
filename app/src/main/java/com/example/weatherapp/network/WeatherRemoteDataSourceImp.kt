package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherResponse

class WeatherRemoteDataSourceImp private constructor(weatherApiService: WeatherApiService):WeatherRemoteDataSource{

    override suspend fun getOneCallResponse(
        lat: Double?,
        lon: Double?,

    ): WeatherResponse {
        return apiService.getWeatherForecast(lat,lon)
    }

    val apiService: WeatherApiService by lazy {
        RetrofitHelper.retrofitInstance.create(WeatherApiService::class.java)
    }

    companion object {
        private var instance: WeatherRemoteDataSourceImp? = null
        fun getInstance(weatherApiService: WeatherApiService): WeatherRemoteDataSourceImp {
            return instance?: synchronized(this){
                instance ?: WeatherRemoteDataSourceImp(weatherApiService)
                    .also { instance = it }
            }
        }
    }
}