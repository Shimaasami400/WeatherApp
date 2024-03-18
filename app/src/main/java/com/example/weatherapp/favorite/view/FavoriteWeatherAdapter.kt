
package com.example.weatherapp.favorite.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FavItemBinding
import com.example.weatherapp.model.FavoriteWeather

class FavoriteWeatherAdapter (private val itemClickListener: (FavoriteWeather) -> Unit, private val deleteClickListener: (FavoriteWeather) -> Unit):
    ListAdapter<FavoriteWeather, FavoriteWeatherViewHolder>(ProductDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteWeatherViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding = FavItemBinding.inflate(inflater, parent, false)
        return FavoriteWeatherViewHolder(binding, itemClickListener, deleteClickListener)
    }

    override fun onBindViewHolder(holder: FavoriteWeatherViewHolder, position: Int) {
        val currentFavoriteWeather = getItem(position)
        holder.bind(currentFavoriteWeather)
    }

}

class FavoriteWeatherViewHolder(private val binding: FavItemBinding,
                                private val itemClickListener: (FavoriteWeather) -> Unit,
                                private val deleteClickListener: (FavoriteWeather) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(favoriteWeather: FavoriteWeather) {
        binding.apply {
            txtLocation.text = favoriteWeather.address
            root.setOnClickListener { itemClickListener(favoriteWeather) }
            deleteImageView.setOnClickListener {
                deleteClickListener(favoriteWeather)
            }

        }
    }
}

class ProductDiffUtil : DiffUtil.ItemCallback<FavoriteWeather>() {
    override fun areItemsTheSame(oldItem: FavoriteWeather, newItem: FavoriteWeather): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: FavoriteWeather, newItem: FavoriteWeather): Boolean {
        return oldItem == newItem
    }
}

