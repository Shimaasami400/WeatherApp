package com.example.weatherapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.model.AlertPojo
import com.example.weatherapp.model.FavoriteWeather

@Database(entities = [FavoriteWeather::class, AlertPojo::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun weatherDataBaseDao(): WeatherDao
}

object DatabaseClient{

        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "favorite_db",
                ).build()
                    .also { instance = it }
            }
        }
}