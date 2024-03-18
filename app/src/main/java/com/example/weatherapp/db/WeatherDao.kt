package com.example.weatherapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.AlertPojo
import com.example.weatherapp.model.FavoriteWeather
import kotlinx.coroutines.flow.Flow
@Dao
interface WeatherDao {
    @Query("SELECT * FROM favorite")
    fun getFavoriteWeather(): Flow<List<FavoriteWeather>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeather(product: FavoriteWeather)

    @Delete
    suspend fun deleteWeather(product: FavoriteWeather)

    @Query("Select * from AlertTable")
    fun  getAllAlerts(): Flow<List<AlertPojo>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlert(alert: AlertPojo)
    @Delete
    suspend fun deleteAlert(alert: AlertPojo)

    @Query("select * from AlertTable where id = :id limit 1")
    fun getAlertWithId(id: String): AlertPojo
}

