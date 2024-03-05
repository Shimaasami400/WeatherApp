package com.example.weatherapp.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.db.WeatherLocalDataSourceImp
import com.example.weatherapp.home.viewmodel.HomeViewModel
import com.example.weatherapp.home.viewmodel.HomeViewModelFactory
import com.example.weatherapp.map.viewmodel.MapsViewModel
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.WeatherRepositoryImp
import com.example.weatherapp.model.convertToDailyWeather
import com.example.weatherapp.model.convertToHourlyWeather
import com.example.weatherapp.network.ResponseState
import com.example.weatherapp.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val mapsViewModel: MapsViewModel by viewModels()
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(WeatherRepositoryImp.getInstance(WeatherRemoteDataSourceImp.getInstance()))
    }
    private val dayAdapter = DayAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView with LinearLayoutManager
        binding.dailyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        //set adapter
        binding.dailyRecyclerView.adapter = dayAdapter

        // Retrieve latitude and longitude from arguments
        val latitude = arguments?.getDouble("latitude") ?: 0.0
        val longitude = arguments?.getDouble("longitude") ?: 0.0

        val isLocationFromMapActivity = arguments?.getBoolean("isLocationFromMapActivity") ?: false

        // Check if the location was sent from the MapActivity
        if (isLocationFromMapActivity) {
            // Location is from MapActivity
            Log.i("HomeFragment", "Location is from MapActivity")
        } else {
            // Location is not from MapActivity
            Log.i("HomeFragment", "Location is not from MapActivity")
        }

        // Fetch weather data for the provided latitude and longitude
        viewModel.getWeatherResponse(latitude, longitude)

        // Log the latitude and longitude
        Log.i("HomeFragment", "Latitude: $latitude, Longitude: $longitude")

        lifecycleScope.launch {
            viewModel.weather.collectLatest { viewStateResult ->
                when (viewStateResult) {
                    is ResponseState.Success -> {
                        val weatherResponse = viewStateResult.data
                        val dailyWeather = weatherResponse.daily

                        // Convert DailyItem objects to DailyWeather objects
                        val convertedDailyWeather = convertToDailyWeather(dailyWeather)

                        // Update the list in the adapter
                        dayAdapter.submitList(convertedDailyWeather)
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
    }

    companion object {
        fun newInstance(latitude: Double, longitude: Double, isLocationFromMapActivity: Boolean): HomeFragment  {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putDouble("latitude", latitude)
            args.putDouble("longitude", longitude)
            args.putBoolean("isLocationFromMapActivity", isLocationFromMapActivity)
            fragment.arguments = args
            return fragment
        }
    }
}