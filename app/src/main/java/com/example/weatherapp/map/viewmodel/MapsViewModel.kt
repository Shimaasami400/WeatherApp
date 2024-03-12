package com.example.weatherapp.map.viewmodel

import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherRepositoryImp
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapsViewModel(private val repository: WeatherRepositoryImp): ViewModel() {

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

    fun setLocation(latLng: LatLng) {
        _selectedLocation.value = latLng
    }

    suspend fun insertFavorite(favoriteWeather: FavoriteWeather){
        repository.insertWeather(favoriteWeather)
    }
}