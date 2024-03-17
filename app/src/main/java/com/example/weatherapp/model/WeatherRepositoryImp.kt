package com.example.weatherapp.model


import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.network.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WeatherRepositoryImp (
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource

):WeatherRepository {

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        language: String,
        units: String?,

    ) : Flow<WeatherResponse> {
        return flowOf(remoteDataSource.getOneCallResponse(latitude, longitude,language,units))
    }


    companion object {
        private var instance: WeatherRepositoryImp? = null
        fun getInstance(remoteSource: WeatherRemoteDataSource,localSource: WeatherLocalDataSource): WeatherRepositoryImp {
            return instance ?: synchronized(this) {
                instance?: WeatherRepositoryImp(remoteSource,localSource)
                    .also { instance = it }
            }
        }
    }

    override fun getAllWeather(): Flow<List<FavoriteWeather>> {
        return localDataSource.getAllWeather()
    }

    override suspend fun insertWeather(favoriteWeather: FavoriteWeather) {
        localDataSource.insertWeather(favoriteWeather)
    }

    override suspend fun deleteWeather(favoriteWeather: FavoriteWeather) {
        localDataSource.deleteWeather(favoriteWeather)
    }

    override fun getAllAlerts(): Flow<List<AlertPojo>> {
        return localDataSource.getAllAlerts()
    }

    override suspend fun insertAlert(alert: AlertPojo) {
       localDataSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
        localDataSource.deleteAlert(alert)
    }

    override fun getAlertWithId(id: String): AlertPojo {
        return localDataSource.getAlertWithId(id)
    }

}