package com.example.weatherapp.home.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherRepository
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel (private val repository: WeatherRepository): ViewModel() {
    private val _weather = MutableStateFlow<ResponseState<WeatherResponse>>(ResponseState.Loading)
    val weather = _weather.asStateFlow()

    private val _currentWeather = MutableLiveData<WeatherResponse>()
    val currentWeather: LiveData<WeatherResponse> = _currentWeather

    fun getWeatherResponse(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeatherForecast(latitude, longitude)
                ?.catch { e -> _weather.value = ResponseState.Error(e) }
                ?.collect { data ->
                    _weather.value = ResponseState.Success(data)
                    Log.i("TAG", "Weather data received: $data")
                }
           /* repository.getWeatherForecast(latitude, longitude)
                .catch { errorMsg ->
                    _weather.value = ResponseState.Error(errorMsg)
                }
                .collect { list ->
                    _weather.value = ResponseState.Success(list)
                }*/
            /*val weather = repository.getWeatherForecast(latitude, longitude)
            withContext(Dispatchers.Main) {
                _weather.value = weather
                _currentWeather.value = weather*/
            Log.i("TAG", "fetchWeatherForecast: $weather")
        }
    }


    /* fun getCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentWeather = repository.getCurrentWeather()
            withContext(Dispatchers.Main) {
                _currentWeather.value = currentWeather
            }
        }
    }*/

}
