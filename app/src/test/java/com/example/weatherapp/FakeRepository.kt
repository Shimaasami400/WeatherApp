
package com.example.weatherapp

import com.example.weatherapp.model.AlertPojo
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherRepository
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository (
    private var fakeRemoteSource: FakeRemoteSource,
    private var fakeLocalSource: FakeLocalSource
) : WeatherRepository{

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        language: String,
        units: String?
    ): Flow<WeatherResponse> {
        return flowOf( fakeRemoteSource.getOneCallResponse(latitude,longitude,language,units))
    }

    override fun getAllWeather(): Flow<List<FavoriteWeather>> {
        return fakeLocalSource.getAllWeather()
    }

    override suspend fun insertWeather(favWeather: FavoriteWeather) {
        fakeLocalSource.insertWeather(favWeather)
    }

    override suspend fun deleteWeather(favWeather: FavoriteWeather) {
        fakeLocalSource.deleteWeather(favWeather)
    }

    override fun getAllAlerts(): Flow<List<AlertPojo>> {
        return fakeLocalSource.getAllAlerts()
    }

    override suspend fun insertAlert(alert: AlertPojo) {
        fakeLocalSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
        fakeLocalSource.deleteAlert(alert)
    }

    override fun getAlertWithId(id: String): AlertPojo {
       return fakeLocalSource.getAlertWithId(id)
    }


}
