package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteWeather (
    @PrimaryKey(autoGenerate = true)
    var roomId:Long =0,
    val address:String ,
    val lat: Double,
    val lng :Double)
