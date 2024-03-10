package com.example.weatherapp.model

import androidx.room.Entity

@Entity(tableName = "fav_sky_watch", primaryKeys = ["lat","lng"])
data class FavoritePojo (val address:String ,val lat: Double,val lng :Double)