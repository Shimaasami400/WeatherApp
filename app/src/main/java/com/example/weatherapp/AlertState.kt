package com.example.weatherapp

sealed class AlertState <out T> {
    data class Success<out T>(val data: List<T>) : AlertState<T>()
    data class Error(val message: Throwable) : AlertState<Nothing>()
    object Loading : AlertState<Nothing>()
}