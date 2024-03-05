package com.example.weatherapp.db

import com.example.weatherapp.network.WeatherRemoteDataSourceImp

class WeatherLocalDataSourceImp {

    companion object {
        private var instance: WeatherLocalDataSourceImp? = null
        fun getInstance(): WeatherLocalDataSourceImp {
            return instance ?: synchronized(this) {
                val temp = WeatherLocalDataSourceImp()
                instance = temp
                temp
            }
        }
    }
}