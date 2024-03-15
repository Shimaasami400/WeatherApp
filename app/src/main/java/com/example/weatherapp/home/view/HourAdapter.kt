package com.example.weatherapp.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.HourItemBinding
import com.example.weatherapp.model.HourlyWeather

class HourAdapter : ListAdapter<HourlyWeather, HourAdapter.HourViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val binding = HourItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HourViewHolder(private val binding: HourItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(hourlyWeather: HourlyWeather) {
            binding.apply {
                txtHour.text = "${hourlyWeather.hour} ${hourlyWeather.amPm}"
                txtTemperature.text = hourlyWeather.temperature.toString()
                //weatherImage.setImageResource(getWeatherIcon(dailyWeather.weatherDescription))

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

    class DiffCallback : DiffUtil.ItemCallback<HourlyWeather>() {
        override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean {
            return oldItem == newItem
        }
    }
}