package com.example.weatherapp.model


import android.util.Log
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.db.WeatherLocalDataSourceImp
import com.example.weatherapp.network.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class WeatherRepositoryImp (
    var remoteDataSource: WeatherRemoteDataSource,
):WeatherRepository{

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,

    ) : Flow<WeatherResponse> {
        return flowOf(remoteDataSource.getOneCallResponse(latitude, longitude))
    }

    //override fun getCurrentWeather(): Flow<List<WeatherResponse>> {

   // }

    /*override  fun getCurrentWeather(): Flow<WeatherResponse> {
        return flow {
            emit(getWeatherForecast(0.0, 0.0))
        }
    }*/

    companion object {
        private var instance: WeatherRepositoryImp? = null
        fun getInstance(remoteSource: WeatherRemoteDataSource): WeatherRepositoryImp {
            return instance ?: synchronized(this) {
                val temp = WeatherRepositoryImp(remoteSource)
                instance = temp
                temp
            }
        }
    }
}