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
                iconDay.setImageResource(getWeatherIcon(dailyWeather.icon))
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

    class DiffCallback : DiffUtil.ItemCallback<DailyWeather>() {
        override fun areItemsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DailyWeather, newItem: DailyWeather): Boolean {
            return oldItem == newItem
        }
    }
}