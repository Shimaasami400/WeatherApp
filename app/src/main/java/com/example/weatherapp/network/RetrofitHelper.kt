package com.example.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitHelper {
    const val BASE_URL = "https://api.openweathermap.org/data/3.0/"
    //const val API_KEY = "adb5f9b065df229b523bbc9ef5b27f49"

     val retrofitInstance : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherApiService : WeatherApiService by lazy {
        retrofitInstance.create(WeatherApiService::class.java)
    }

}

