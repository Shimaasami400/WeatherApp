package com.example.weatherapp



sealed class FavoriteState<out T> {
    data class Success<out T>(val data: List<T>) : FavoriteState<T>()
    data class Error(val message: Throwable) : FavoriteState<Nothing>()
    object Loading : FavoriteState<Nothing>()
}