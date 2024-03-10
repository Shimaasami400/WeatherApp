package com.example.weatherapp.helper

import android.content.Context
import android.location.Address
import android.location.Geocoder

fun getAddressEnglish(context: Context, lat: Double?, lon: Double?):String{
    var address:MutableList<Address>?
    val geocoder= Geocoder(context)
    address =geocoder.getFromLocation(lat!!,lon!!,1)
    if (address?.isEmpty()==true) {
        return "Unkown location"
    } else if (address?.get(0)?.countryName.isNullOrEmpty()) {
        return "Unkown Country"
    } else if (address?.get(0)?.adminArea.isNullOrEmpty()) {
        return address?.get(0)?.countryName.toString()
    } else{
        return address?.get(0)?.countryName.toString()+", "+address?.get(0)?.adminArea+", "+address?.get(0)?.locality
    }
}