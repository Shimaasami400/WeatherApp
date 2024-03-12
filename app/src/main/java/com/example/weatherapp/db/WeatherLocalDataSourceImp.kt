package com.example.weatherapp.db

import androidx.core.content.ContentProviderCompat.requireContext
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImp (private val weatherDao: WeatherDao) : WeatherLocalDataSource {

    companion object {
        @Volatile
        var instance: WeatherLocalDataSourceImp? = null
        fun getInstance(weatherDao: WeatherDao): WeatherLocalDataSourceImp {
            return instance?: synchronized(this){
                instance?: WeatherLocalDataSourceImp(weatherDao)
                    .also { instance = it }
            }
        }
    }

    override fun getAllWeather(): Flow<List<FavoriteWeather>> {
        return weatherDao.getFavoriteWeather()
    }

    override suspend fun insertWeather(favoriteItem: FavoriteWeather) {
        weatherDao.insertWeather(favoriteItem)
    }

    override suspend fun deleteWeather(favoriteItem: FavoriteWeather) {
        weatherDao.deleteWeather(favoriteItem)
    }

}