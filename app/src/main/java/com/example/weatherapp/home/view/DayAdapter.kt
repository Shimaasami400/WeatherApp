package com.example.weatherapp.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DayItemBinding
import com.example.weatherapp.model.DailyWeather

class DayAdapter : ListAdapter<DailyWeather, DayAdapter.DayViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = DayItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DayViewHolder(private val binding: DayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dailyWeather: DailyWeather) {
            binding.apply {
                txtDayName.text = dailyWeather.dayOfWeek
                txtDescDay.text = dailyWeather.weatherDescription
                txtMaxTempDay.text = dailyWeather.maxTemperature.toString()
                txtMinTempDay.text = dailyWeather.minTemperature.toString()
                // Set the appropriate weather icon based on the weather description
                iconDay.setImageResource(getWeatherIcon(dailyWeather.weatherDescription))
            }
        }

        private fun getWeatherIcon(weatherDescription: String): Int {
            return when (weatherDescription.toLowerCase()) {
                "sunny" -> R.drawable.sunny
                "thunderstorm" -> R.drawable.storm
                "few clouds" -> R.drawable.cloudy
                "scattered clouds" -> R.drawable.cloudy
                "broken clouds" -> R.drawable.cloudy
                "light rain" -> R.drawable.rain
                "moderate rain" -> R.drawable.rain
                "snow" -> R.drawable.snow
                "mist" -> R.drawable.mist
                else -> R.drawable.clear_sky
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DailyWeather>() {
        override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem == newItem
        }
    }
}