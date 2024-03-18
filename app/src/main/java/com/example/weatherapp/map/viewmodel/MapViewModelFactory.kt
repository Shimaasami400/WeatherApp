package com.example.weatherapp.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.favorite.viewmodel.FavoriteViewModel
import com.example.weatherapp.model.WeatherRepositoryImp

class MapViewModelFactory (private val _repo: WeatherRepositoryImp) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            MapsViewModel(_repo) as T
        } else {
            throw java.lang.IllegalArgumentException("ViewModel Class not found")
        }
    }
}