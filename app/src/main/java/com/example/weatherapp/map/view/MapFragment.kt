package com.example.weatherapp.map.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherapp.Constants
import com.example.weatherapp.databinding.FragmentMapBinding
import com.example.weatherapp.db.DatabaseClient
import com.example.weatherapp.db.WeatherLocalDataSourceImp
import com.example.weatherapp.helper.getAddress
import com.example.weatherapp.map.viewmodel.MapViewModelFactory
import com.example.weatherapp.map.viewmodel.MapsViewModel
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.LocationLatLngPojo
import com.example.weatherapp.model.WeatherRepositoryImp
import com.example.weatherapp.network.RetrofitHelper
import com.example.weatherapp.network.WeatherRemoteDataSourceImp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch


class MapFragment : Fragment() , OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var locationSharedPreferences: SharedPreferences
    private lateinit var binding : FragmentMapBinding
    private var selectedLatLng = LatLng(-34.0, 151.0)

    private val viewModel: MapsViewModel by viewModels {
        MapViewModelFactory(
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
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        locationSharedPreferences = (activity as AppCompatActivity?)
            ?.getSharedPreferences("LocationUsedMethod", Context.MODE_PRIVATE)!!

        var type = MapFragmentArgs.fromBundle(requireArguments()).type
        when (type) {
            "Home" -> {
                binding.mapBtn.setOnClickListener {
                    var action = MapFragmentDirections.actionMapFragmentToHomeFragment().apply {
                        this.latLang = LocationLatLngPojo(
                            "Map",
                            selectedLatLng.latitude,
                            selectedLatLng.longitude
                        )

                        locationSharedPreferences.edit().putFloat(Constants.MAP_LAT,selectedLatLng.latitude.toFloat())
                        locationSharedPreferences.edit().putFloat(Constants.MAP_LON,selectedLatLng.longitude.toFloat())

                        Log.i(
                            "TAG",
                            "onViewCreated: LocationLatLngPojo :{ $selectedLatLng.latitude,$selectedLatLng.longitude}"
                        )
                    }
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }
            "Favorite" -> {
                binding.mapBtn.setOnClickListener {
                    lifecycleScope.launch {
                        val favoriteWeather = FavoriteWeather(
                            address = getAddress(requireActivity(), selectedLatLng.latitude, selectedLatLng.longitude),
                            lat = selectedLatLng.latitude,
                            lng = selectedLatLng.longitude
                        )
                        viewModel.insertFavorite(favoriteWeather)
                        Toast.makeText(requireContext(), "Location saved to favorites", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            "Setting" -> {
                binding.mapBtn.setOnClickListener {
                    var action = MapFragmentDirections.actionMapFragmentToHomeFragment().apply {
                        this.latLang = LocationLatLngPojo(
                            "Map",
                            selectedLatLng.latitude,
                            selectedLatLng.longitude
                        )
                        locationSharedPreferences.edit().putFloat(Constants.MAP_LAT,selectedLatLng.latitude.toFloat())
                        locationSharedPreferences.edit().putFloat(Constants.MAP_LON,selectedLatLng.longitude.toFloat())
                        Log.i(
                            "MapFragment",
                            "onViewCreated  -----------Setting: LocationLatLngPojo :{ $selectedLatLng.latitude,$selectedLatLng.longitude}"
                        )
                    }
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }

            Constants.ALERT_KEY ->{
                binding.mapBtn.setOnClickListener {
                    var action = MapFragmentDirections.actionMapFragmentToAlertsFragment2()
                                action.latlon = LocationLatLngPojo(
                                    "Map",
                                    selectedLatLng.latitude,
                                    selectedLatLng.longitude
                                )
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val currentLocation = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(currentLocation).title("Your Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        binding.mapView.getMapAsync(){
            it.addMarker(MarkerOptions().position(currentLocation).title("Your Location"))
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        }
        map.setOnMapClickListener { latLng ->
            // Move the camera to the clicked position
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            // Clear existing markers (if any)
            googleMap.clear()
            selectedLatLng=latLng
            // Add a marker at the clicked position
            googleMap.addMarker(MarkerOptions().position(latLng).title(getAddress(requireActivity(),
                map.cameraPosition.target.latitude,
                map.cameraPosition.target.longitude)).snippet("Marker Snippet"))
        }
        map.setOnCameraMoveListener {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(
                LatLng(
                    map.cameraPosition.target.latitude, map.cameraPosition.target.longitude
                )
            ))

            // Clear existing markers (if any)
            googleMap.clear()
            selectedLatLng =LatLng(
                map.cameraPosition.target.latitude, map.cameraPosition.target.longitude
            )
            // Add a marker at the clicked position
            googleMap.addMarker(MarkerOptions().position(
                LatLng(
                    map.cameraPosition.target.latitude, map.cameraPosition.target.longitude
                )
                //getAddressEnglish(requireActivity(),map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
            ).title("location").snippet("Marker Snippet"))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }


}