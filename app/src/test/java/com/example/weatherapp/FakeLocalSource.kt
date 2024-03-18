package com.example.weatherapp

import com.example.weatherapp.model.AlertPojo
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.model.FavoriteWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class FakeLocalSource (
    private var favList:MutableList<FavoriteWeather> = mutableListOf(),
    private var alertList:MutableList<AlertPojo> = mutableListOf()
    ) :WeatherLocalDataSource{

    override fun getAllWeather(): Flow<List<FavoriteWeather>> {
        return flowOf(favList)
    }

    override suspend fun insertWeather(favWeather: FavoriteWeather) {
        favList.add(favWeather)
    }

    override suspend fun deleteWeather(favWeather: FavoriteWeather) {
        favList.remove(favWeather)
    }

    override fun getAllAlerts(): Flow<List<AlertPojo>> {
        return flowOf(alertList)
    }

    override suspend fun insertAlert(alert: AlertPojo) {
        alertList.add(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
        alertList.remove(alert)
    }

    override fun getAlertWithId(id: String): AlertPojo {
        return alertList.find { it.id == id }
            ?: throw NoSuchElementException("No AlertPojo found with ID: $id")
    }
}
