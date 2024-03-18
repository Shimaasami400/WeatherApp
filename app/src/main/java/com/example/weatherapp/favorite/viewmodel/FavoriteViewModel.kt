package com.example.weatherapp.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.FavoriteState
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherRepository
import com.example.weatherapp.model.WeatherRepositoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteViewModel (private val repository: WeatherRepository) : ViewModel(){

    private val _favoriteList = MutableStateFlow <FavoriteState<FavoriteWeather>>(FavoriteState.Loading)
    val favoriteList = _favoriteList.asStateFlow()

    fun getFavoriteWeather() {
        viewModelScope.launch(Dispatchers.IO){
            repository.getAllWeather()
                .catch {
                        error -> _favoriteList.value = FavoriteState.Error(error)
                        Log.i("FavoriteViewModel", "Error getting favorite weather: $error")
                }
                .collect{
                        list -> _favoriteList.value = FavoriteState.Success(list)
                        Log.i("FavoriteViewModel", "Favorite weather list retrieved: $list")

                }
        }
    }

    fun deleteWeatherLocation(favoriteWeather: FavoriteWeather){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWeather(favoriteWeather)
        }
    }

}