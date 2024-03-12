package com.example.weatherapp.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.db.DatabaseClient
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.db.WeatherLocalDataSourceImp
import com.example.weatherapp.home.viewmodel.HomeViewModel
import com.example.weatherapp.home.viewmodel.HomeViewModelFactory
import com.example.weatherapp.map.view.MapActivity
import com.example.weatherapp.map.viewmodel.MapsViewModel
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.WeatherRepositoryImp
import com.example.weatherapp.model.convertToDailyWeather
import com.example.weatherapp.model.convertToHourlyWeather
import com.example.weatherapp.network.ResponseState
import com.example.weatherapp.network.RetrofitHelper
import com.example.weatherapp.network.WeatherRemoteDataSourceImp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val PERMISSION_ID = 1001
const val LOCATION_DIALOG_SHOWN = "locationDialogShown"

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var locationManager: LocationManager
    private var isLocationReceived = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(RetrofitHelper.weatherApiService),
                WeatherLocalDataSourceImp.getInstance(DatabaseClient.getInstance(requireContext()).favoriteWeather())
            )
        )
    }
    private val dayAdapter = DayAdapter()
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("LocationUsedMethod", Context.MODE_PRIVATE)

        binding.dailyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dailyRecyclerView.adapter = dayAdapter

        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val args = HomeFragmentArgs.fromBundle(requireArguments())
        Log.i("HomeFragment", "Received args: ${args.latLang}")
        val latitude = args.latLang?.lat
        val longitude = args.latLang?.lng
        Log.i("HomeFragment", "Received latitude: $latitude, longitude: $longitude")

        if (latitude != null && longitude != null) {
            if (!isLocationReceived) {
                setUpUI(LatLng(latitude, longitude))
                Log.i("HomeFragment", "Received after if statement: latitude: $latitude, longitude: $longitude")
                isLocationReceived = true
            }
        } else {

            val isLocationFromMapActivity = arguments?.getBoolean("isLocationFromMapActivity") ?: false

            if (isLocationFromMapActivity) {
                Log.i("HomeFragment", "Location is from MapActivity")
            } else {
                Log.i("HomeFragment", "Location is not from MapActivity")
                val locationDialogShown = sharedPreferences.getBoolean(LOCATION_DIALOG_SHOWN, false)
                Log.i("HomeFragment", "Location dialog shown: $locationDialogShown")
                if (!locationDialogShown) {
                    showLocationDialog()
                    sharedPreferences.edit().putBoolean(LOCATION_DIALOG_SHOWN, true).apply()
                    sharedPreferences.edit().putString("Location_Method", "Use GPS").apply()
                    Log.i("TAG", "onViewCreated: Dialog")
                } else {
                    val locationMethod = sharedPreferences.getString("Location_Method", "Use GPS")
                    handleLocationMethod(locationMethod)
                    Log.i("TAG", "onViewCreated: not Dialog")
                }
            }
        }
    }

    private fun showLocationDialog() {
        val items = arrayOf("Use GPS", "Open Map")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select location method")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> {
                        useGPS()
                        Log.i("TAG", "showLocationDialog: =============================== GPS")
                    }
                    1 -> {
                        openMap()
                        Log.i("TAG", "showLocationDialog: =============================== MAP")

                    }
                }
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun useGPS() {
        getLocation()
    }

    private fun checkPermission(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return result
    }

    private fun openMap() {
        var tye = "Home"
        var action :HomeFragmentDirections.ActionHomeFragmentToMapFragment =
            HomeFragmentDirections.actionHomeFragmentToMapFragment().apply {
            type = tye
            }
        Navigation.findNavController(requireView()).navigate(action)
    }

    private val locationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (!isLocationReceived) {
                val lastLocation: Location = locationResult.lastLocation
                currentLatitude = lastLocation.latitude
                currentLongitude = lastLocation.longitude

                Log.i("HomeFragment", "Latitude: $currentLatitude, Longitude: $currentLongitude")

                setUpUI(LatLng(lastLocation.latitude,lastLocation.longitude))
                isLocationReceived = true
                fusedLocationProviderClient.removeLocationUpdates(this)
            }
        }
    }

    fun setUpUI(latLang :LatLng){
        viewModel.getWeatherResponse(latLang.latitude, latLang.longitude)
        lifecycleScope.launch {
            viewModel.weather.collectLatest { viewStateResult ->
                when (viewStateResult) {
                    is ResponseState.Success -> {
                        val weatherResponse = viewStateResult.data
                        val currentWeather = weatherResponse.current

                        binding.currentWeatherLocation.text = "${weatherResponse.timezone}"
                        binding.currentWeatherTemperature.text = "${currentWeather.temp} °K"
                        binding.currentWeatherDescription.text = "${currentWeather.weather.firstOrNull()?.description}"
                        val weatherIcon = currentWeather.weather.firstOrNull()?.description.toString()
                        binding.currentWeatherImageView.setImageResource(getWeatherIcon(weatherIcon))

                        val dailyWeather = weatherResponse.daily
                        val convertedDailyWeather = convertToDailyWeather(dailyWeather)
                        dayAdapter.submitList(convertedDailyWeather)
                    }

                    is ResponseState.Loading -> {
                    }

                    else -> {
                        Log.i("TAG", "onViewCreated: Something went wrong.")
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()

        val argsLatLng =  HomeFragmentArgs.fromBundle(requireArguments()).latLang
        if (argsLatLng != null) {
            setUpUI(LatLng(argsLatLng.lat, argsLatLng.lng))
            Log.i("TAG", "onResume: ================")
        } else {
            val locationDialogShown = sharedPreferences.getBoolean(LOCATION_DIALOG_SHOWN, false)
            Log.i("HomeFragment", "Location dialog shown: $locationDialogShown")

            if (!locationDialogShown) {
                showLocationDialog()
                sharedPreferences.edit().putBoolean(LOCATION_DIALOG_SHOWN, true).apply()
                sharedPreferences.edit().putString("Location_Method", "Use GPS").apply()
                Log.i("TAG", "onResume: Dialog")
            } else {
                val locationMethod = sharedPreferences.getString("Location_Method", "Use GPS")
                handleLocationMethod(locationMethod)
                Log.i("TAG", "onResume: not Dialog")
            }
        }
    }

    private fun handleLocationMethod(locationMethod: String?) {
        sharedPreferences.edit().putString("Location_Method", locationMethod).apply()
        when (locationMethod) {
            "Use GPS" -> useGPS()
            "Open Map" -> openMap()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())
    }

    private fun getLocation(): Unit {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationProviderClient.requestLocationUpdates(
                    createLocationRequest(),
                    locationCallBack,
                    Looper.getMainLooper()
                )
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun getWeatherIcon(weatherDescription: String): Int {
        return when (weatherDescription.toLowerCase()) {
            "sunny" -> R.drawable.sunny
            "thunderstorm" -> R.drawable.storm
            "few clouds" -> R.drawable.cloudy
            "scattered clouds" -> R.drawable.cloudy
            "broken clouds" -> R.drawable.cloudy
            "rain" -> R.drawable.rain
            "shower rain" -> R.drawable.rain
            "snow" -> R.drawable.snow
            "mist" -> R.drawable.mist
            else -> R.drawable.clear_sky
        }
    }

    companion object {
        fun newInstance(latitude: Double, longitude: Double, isLocationFromMapActivity: Boolean): HomeFragment {
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
