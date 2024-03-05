package com.example.weatherapp.map.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapsViewModel : ViewModel() {

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation


    //private var selectedLocation: LatLng? = null

    fun setLocation(latLng: LatLng) {
        _selectedLocation.value = latLng
    }

    fun saveLocationToFav() {
        // Implement saving location to favorites
    }

    fun saveLocationAsAlarm() {
        // Implement saving location as alarm
    }

    fun saveLocationAsMainLocation() {
        // Implement saving location as main location
    }
}