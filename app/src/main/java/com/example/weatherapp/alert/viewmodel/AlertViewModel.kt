package com.example.weatherapp.alert.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.AlertState
import com.example.weatherapp.model.AlertPojo
import com.example.weatherapp.model.WeatherRepositoryImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel (private val repository: WeatherRepositoryImp) : ViewModel() {

    private val _alertList =
        MutableStateFlow<AlertState<AlertPojo>>(AlertState.Loading)
    val alertList = _alertList.asStateFlow()

    fun getAlertWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllAlerts()
                .catch { error ->
                    _alertList.value = AlertState.Error(error)
                    Log.i("AlertViewModel", "Error getting Alert weather: $error")
                }
                .collect { list ->
                    _alertList.value = AlertState.Success(list)
                    Log.i("AlertViewModel", "Alert weather list retrieved: $list")

                }
        }
    }

    fun insertWeatherAlert(alertPojo: AlertPojo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlert(alertPojo)
        }

    }


    fun deleteWeatherAlert(alertPojo: AlertPojo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlert(alertPojo)
        }

    }
}
