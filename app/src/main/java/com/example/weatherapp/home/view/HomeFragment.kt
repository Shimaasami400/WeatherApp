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
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Constants
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.db.DatabaseClient
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.db.WeatherLocalDataSourceImp
import com.example.weatherapp.helper.getAddress
import com.example.weatherapp.helper.toArabicNumerals
import com.example.weatherapp.home.viewmodel.HomeViewModel
import com.example.weatherapp.home.viewmodel.HomeViewModelFactory
import com.example.weatherapp.map.view.MapActivity
import com.example.weatherapp.map.viewmodel.MapsViewModel
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.WeatherRepositoryImp
import com.example.weatherapp.model.WeatherResponse
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
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.DecimalFormat

const val PERMISSION_ID = 1001
const val LOCATION_DIALOG_SHOWN = "locationDialogShown"

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var locationManager: LocationManager
    private var isLocationReceived = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesSettings: SharedPreferences

    private lateinit var selectedLanguage: String
    private lateinit var selectedUnit: String

    private val dayAdapter = DayAdapter()
    private var hourAdapter = HourAdapter()
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(RetrofitHelper.weatherApiService),
                WeatherLocalDataSourceImp.getInstance(DatabaseClient.getInstance(requireContext()).weatherDataBaseDao())
            )
        )
    }

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

        sharedPreferencesSettings = requireActivity().getSharedPreferences(
            Constants.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )

        selectedLanguage = sharedPreferencesSettings.getString(
            Constants.LANGUAGE_KEY,
            Constants.Enum_lANGUAGE.en.toString()
        ).toString()

        selectedUnit = sharedPreferencesSettings.getString(
            Constants.UNITS_KEY,
            Constants.ENUM_UNITS.metric.toString()
        ).toString()

        binding.dailyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dailyRecyclerView.adapter = dayAdapter

        binding.recyclerViewForTime.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerViewForTime.adapter = hourAdapter

        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val args = HomeFragmentArgs.fromBundle(requireArguments())
        Log.i("HomeFragment", "Received args: ${args.latLang}")
        val latitude = args.latLang?.lat
        val longitude = args.latLang?.lng
        Log.i("HomeFragment", "Received latitude: $latitude, longitude: $longitude")

        if (latitude != null && longitude != null) {
            if (!isLocationReceived) {
                setUpUI(LatLng(latitude, longitude),selectedLanguage,selectedUnit)
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

                setUpUI(LatLng(lastLocation.latitude,lastLocation.longitude),selectedLanguage,selectedUnit)
                isLocationReceived = true
                fusedLocationProviderClient.removeLocationUpdates(this)
            }
        }
    }

    fun setUpUI(latLang: LatLng, language: String, units: String) {
        val address = getAddress(requireContext(), latLang.latitude, latLang.longitude)
        binding.currentWeatherLocation.text = address

        if (isConnected(requireContext())) {
            viewModel.getWeatherResponse(latLang.latitude, latLang.longitude, language, units)
            lifecycleScope.launch {
                viewModel.weather.collectLatest { viewStateResult ->
                    when (viewStateResult) {
                        is ResponseState.Success -> {
                            val weatherResponse = viewStateResult.data
                            displayWeatherData(weatherResponse, language, units)
                        }
                        is ResponseState.Loading -> {
                            // Show loading indicator if needed
                        }
                        is ResponseState.Error -> {
                            displayError(viewStateResult.message.toString())
                            loadWeatherDataFromFile()
                        }
                    }
                }
            }
        } else {
            // If not connected, load data from file
            loadWeatherDataFromFile()
        }
    }

    private fun displayWeatherData(weatherResponse: WeatherResponse, language: String, units: String) {
        val currentWeather = weatherResponse.current

        val temperatureUnit = when (language) {
            Constants.Enum_lANGUAGE.ar.toString() -> {
                when (units) {
                    Constants.ENUM_UNITS.metric.toString() -> "°س"
                    Constants.ENUM_UNITS.imperial.toString() -> "°ف"
                    Constants.ENUM_UNITS.standard.toString() -> "ك"
                    else -> "°س"
                }
            }
            else -> {
                when (units) {
                    Constants.ENUM_UNITS.metric.toString() -> "°C"
                    Constants.ENUM_UNITS.imperial.toString() -> "°F"
                    Constants.ENUM_UNITS.standard.toString() -> "K"
                    else -> "°C"
                }
            }
        }

        val temperatureValue = if (language == Constants.Enum_lANGUAGE.ar.toString()) {
            currentWeather.temp.toString().toArabicNumerals()
        } else {
            currentWeather.temp.toString()
        }
        binding.currentWeatherTemperature.text = "$temperatureValue $temperatureUnit"
        binding.currentWeatherDescription.text = "${currentWeather.weather.firstOrNull()?.description}"

        binding.txtHumidityValue.text = currentWeather.humidity.toString()
        binding.txtSunriseValue.text = currentWeather.sunrise.toString()
        binding.txtSunsetValue.text = currentWeather.sunset.toString()
        binding.txtUviValue.text = currentWeather.uvi.toString()
        binding.txtWindValue.text = currentWeather.windSpeed.toString()
        binding.txtTemperatureValue.text = currentWeather.pressure.toString()

        val weatherIcon = currentWeather.weather.get(0).icon
        binding.currentWeatherImageView.setImageResource(getWeatherIcon(weatherIcon))

        val dailyWeather = weatherResponse.daily
        val hourlyWeather = weatherResponse.hourly

        val convertedDailyWeather = convertToDailyWeather(dailyWeather)
        val convertHourlyWeather = convertToHourlyWeather(hourlyWeather)
        dayAdapter.submitList(convertedDailyWeather)
        hourAdapter.submitList(convertHourlyWeather)
    }

    private fun displayError(errorMessage: String) {
        // Handle error and display message
    }

    private fun loadWeatherDataFromFile() {
        val weatherData = readWeatherDataFromFile()
        weatherData?.let {
            displayWeatherData(it, "", "")
        }
    }

// Rest of your code...

    override fun onResume() {
        super.onResume()

        val argsLatLng =  HomeFragmentArgs.fromBundle(requireArguments()).latLang
        if (argsLatLng != null) {
            setUpUI(LatLng(argsLatLng.lat, argsLatLng.lng),selectedLanguage,selectedUnit)
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


    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnected
    }




/*private fun saveLastKnownLocation(latitude: Double, longitude: Double) {
        sharedPreferences.edit().apply {
            putFloat(LAST_KNOWN_LOCATION_LATITUDE, latitude.toFloat())
            putFloat(LAST_KNOWN_LOCATION_LONGITUDE, longitude.toFloat())
            apply()
        }
    }*/

    private suspend fun saveWeatherDataToFile(data: WeatherResponse) {
        withContext(Dispatchers.IO) {
            val fileName = "weather_data.txt"
            val file = File(requireContext().filesDir, fileName)

            try {
                val gson = Gson()
                val jsonString = gson.toJson(data)
                file.writeText(jsonString)
            } catch (e: IOException) {
                Log.e("HomeFragment", "Error saving weather data: ${e.message}")
            }
        }
    }

    private  fun readWeatherDataFromFile(): WeatherResponse? {
        val fileName = "weather_data.txt"
        val file = File(requireContext().filesDir, fileName)
        return try {
            if (file.exists()) {
                val jsonString = file.readText()
                val gson = Gson()
                gson.fromJson(jsonString, WeatherResponse::class.java)
            } else {
                Log.e("HomeFragment", "Error: File does not exist")
                null
            }
        } catch (e: IOException) {
            Log.e("HomeFragment", "Error reading weather data from file: IOException", e)
            null
        }
    }

    private fun getWeatherIcon(icon: String): Int {
        val iconValue: Int
        when (icon) {
            "01d" -> iconValue = R.drawable.clear_sky
            "01n" -> iconValue = R.drawable.clear_sky
            "02d" -> iconValue = R.drawable.cloudy
            "02n" -> iconValue = R.drawable.cloudy
            "03n" -> iconValue = R.drawable.cloudy
            "03d" -> iconValue = R.drawable.cloudy
            "04d" -> iconValue = R.drawable.cloudy
            "04n" -> iconValue = R.drawable.cloudy
            "09d" -> iconValue = R.drawable.rain
            "09n" -> iconValue = R.drawable.rain
            "10d" -> iconValue = R.drawable.rain
            "10n" -> iconValue = R.drawable.rain
            "11d" -> iconValue = R.drawable.storm
            "11n" -> iconValue = R.drawable.storm
            "13d" -> iconValue = R.drawable.snow
            "13n" -> iconValue = R.drawable.snow
            "50d" -> iconValue = R.drawable.mist
            "50n" -> iconValue = R.drawable.mist
            else -> iconValue = R.drawable.header
        }
        return iconValue
    }
}


