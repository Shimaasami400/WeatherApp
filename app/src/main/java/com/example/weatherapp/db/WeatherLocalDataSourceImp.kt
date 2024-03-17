package com.example.weatherapp.db

import com.example.weatherapp.model.AlertPojo
import com.example.weatherapp.model.FavoriteWeather
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

    override fun getAllAlerts(): Flow<List<AlertPojo>> {
        return weatherDao.getAllAlerts()
    }

    override suspend fun insertAlert(alert: AlertPojo) {
        weatherDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
        weatherDao.deleteAlert(alert)
    }

    override fun getAlertWithId(id: String): AlertPojo {
       return weatherDao.getAlertWithId(id)
    }

}