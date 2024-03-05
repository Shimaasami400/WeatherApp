package com.example.weatherapp.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.db.WeatherLocalDataSourceImp
import com.example.weatherapp.home.viewmodel.HomeViewModel
import com.example.weatherapp.home.viewmodel.HomeViewModelFactory
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.WeatherRepositoryImp
import com.example.weatherapp.model.convertToDailyWeather
import com.example.weatherapp.model.convertToHourlyWeather
import com.example.weatherapp.network.ResponseState
import com.example.weatherapp.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.weather.collectLatest { viewStateRecult ->
                when (viewStateRecult) {
                    is ResponseState.Success -> {
                        val weatherResponse = viewStateRecult.data
                        val currentWeather = weatherResponse.current
                        val dailyWeather = weatherResponse.daily
                        val hourlyWeather = weatherResponse.hourly

                        // Convert HourlyItem objects to HourlyWeather objects
                        val convertedHourlyWeather = convertToHourlyWeather(hourlyWeather)

                        // Log converted hourly weather data
                        Log.i("HomeFragment", "Converted Hourly weather: $convertedHourlyWeather")

                        // Display the hour, AM/PM, and temperature
                        convertedHourlyWeather.forEach { hourly ->
                            Log.i("HomeFragment", "Hour: ${hourly.hour} ${hourly.amPm}, Temperature: ${hourly.temperature}")
                        }


                        val convertedDailyWeather = convertToDailyWeather(dailyWeather)

                        Log.i("HomeFragment", "Converted Daily weather: $convertedDailyWeather")


                        convertedDailyWeather.forEach { daily ->
                            Log.i("HomeFragment", "Day: ${daily.dayOfWeek}, Date: ${daily.date}, Max Temp: ${daily.maxTemperature}, Min Temp: ${daily.minTemperature}")
                        }
                    }

                    is ResponseState.Loading -> {
                        // Handle loading state if needed
                    }

                    else -> {
                        Log.i("TAG", "onViewCreated: Something went wrong.")
                    }
                }
            }
        }
        viewModel.getWeatherResponse(50.0, 50.0)
    }
}

    /*private fun getNextFiveDaysWeather(weatherResponse: WeatherResponse): List<WeatherData> {
        Log.i("Next 5 Days Weather", "Entering getNextFiveDaysWeather function")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Calendar.getInstance()
        val nextFiveDaysWeather = mutableListOf<WeatherData>()

        val endOfFiveDays = currentDate.apply { add(Calendar.DAY_OF_MONTH, 5) }

        nextFiveDaysWeather.addAll(
            weatherResponse.list.filter { weatherData ->
                val weatherDate = Calendar.getInstance().apply {
                    time = dateFormat.parse(weatherData.dtTxt)
                    Log.i("Next 5 Days Weather", "Weather date: ${time.time}")
                }
                val isWithinRange = weatherDate in currentDate..endOfFiveDays
                isWithinRange
            }
        )

        Log.i("TAG", "getNextFiveDaysWeather: $nextFiveDaysWeather")
        return nextFiveDaysWeather
    }*/


/*
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch{
            viewModel.weather.collectLatest {
                    viewStateRecult ->
                when (viewStateRecult){

                    is ResponseState.Success -> {
                        val weatherResponse = viewStateRecult.data
                        val nextFiveDaysWeather = getNextFiveDaysWeather(weatherResponse)

                        Log.i("HomeFragment", "Weather data: $weatherResponse")
                        nextFiveDaysWeather.forEach { weatherData ->
                            Log.d("Next 5 Days Weather", "Date: ${weatherData.dtTxt}, Temperature: ${weatherData.main.temp}")
                        }
                    }
                    is ResponseState.Loading -> {
                    }
                    else -> {
                        Log.i("TAG", "onViewCreated:Sth went wrong. ")
                    }
                }
            }
        }
        viewModel.getWeatherResponse(0.0,0.0)
    }}*/