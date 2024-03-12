package com.example.weatherapp.favorite.view


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.FavoriteState
import com.example.weatherapp.databinding.FragmentFavoriteBinding
import com.example.weatherapp.db.DatabaseClient
import com.example.weatherapp.db.WeatherLocalDataSourceImp
import com.example.weatherapp.favorite.viewmodel.FavoriteViewModel
import com.example.weatherapp.favorite.viewmodel.FavoriteWeatherViewModelFactory
import com.example.weatherapp.helper.getAddress
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherRepositoryImp
import com.example.weatherapp.network.RetrofitHelper
import com.example.weatherapp.network.WeatherRemoteDataSourceImp
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteAdapter: FavoriteWeatherAdapter


    private val viewModel: FavoriteViewModel by viewModels {
        FavoriteWeatherViewModelFactory(
            WeatherRepositoryImp.getInstance(
                WeatherRemoteDataSourceImp.getInstance(RetrofitHelper.weatherApiService),
                WeatherLocalDataSourceImp.getInstance(DatabaseClient.getInstance(requireContext()).favoriteWeather())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        recyclerView = binding.favoriteRecyclerView
        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        //initialize it with empty click listener
        favoriteAdapter = FavoriteWeatherAdapter({},{})
        recyclerView.adapter = favoriteAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavoriteWeather()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteList.collect { state ->
                when (state) {
                    is FavoriteState.Loading -> {
                        //loading state
                        Log.i("FavoriteFragment", "Loading state")
                        recyclerView.visibility = View.GONE

                    }
                    is FavoriteState.Success -> {
                        // Hide loading state and update RecyclerView with the list of favorite items
                        recyclerView.visibility = View.VISIBLE
                        favoriteAdapter.submitList(state.data)
                        Log.i("FavoriteFragment", "Success state: ${state.data}")
                    }
                    is FavoriteState.Error -> {
                        //  error message
                        Log.i("FavoriteFragment", "Error: ${state.message}")

                    }
                }
            }
        }



        binding.favFab.setOnClickListener {
            val typ = "Favorite"
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToMapFragment().apply {
                type = typ
            }
            findNavController().navigate(action)

        }

        val onItemClick: (FavoriteWeather) -> Unit = { favoriteWeather ->
            Log.i("FavoriteFragment", "Latitude: ${favoriteWeather.lat}, Longitude: ${favoriteWeather.lng}")
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToHomeFragment().apply {
                favoriteWeather.lat;
                favoriteWeather
                latLang = com.example.weatherapp.model.LocationLatLngPojo("fav_location",favoriteWeather.lat, favoriteWeather.lng)
            }


            findNavController().navigate(action)
            Log.i("FavoriteFragment", "Navigating to HomeFragment")
        }

        val onDeleteClick: (FavoriteWeather) -> Unit = { favoriteWeather ->
            viewModel.deleteWeatherLocation(favoriteWeather)
        }

        favoriteAdapter = FavoriteWeatherAdapter(onItemClick, onDeleteClick)
        recyclerView.adapter = favoriteAdapter
    }
}