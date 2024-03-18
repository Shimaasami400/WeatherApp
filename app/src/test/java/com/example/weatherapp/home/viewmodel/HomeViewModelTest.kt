package com.example.weatherapp.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.FakeLocalSource
import com.example.weatherapp.FakeRemoteSource
import com.example.weatherapp.FakeRepository
import com.example.weatherapp.model.Current
import com.example.weatherapp.model.WeatherItem
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HomeViewModelTest{
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var homeViewModel: HomeViewModel
    lateinit var fakeRepository: FakeRepository


    var  currentWeather = Current(
        1000076746,
        50.5,
        10000,
        5.5.toFloat(),
        20,
        20,
        20.5,
        100000000,
        100,
        1100.5,
        16110000,
        listOf(WeatherItem("sun", "sunny", "Clear sky", 500)),
        100,
        3
    )

    var weatherResponse1 = WeatherResponse(
        currentWeather,
        "Egypt/Cairo",
        155000,
        emptyList(),
        -74.006,
        emptyList(),
        40.7128,
    )

    private  var remoteSource = weatherResponse1

    @Before
    fun setup() {
        fakeRepository = FakeRepository(FakeRemoteSource(remoteSource), FakeLocalSource())
        homeViewModel = HomeViewModel(fakeRepository)
    }

    @Test
    fun getWeatherResponse_Success() = runBlockingTest  {
        //Given
       val latitude: Double = -125.00
       val longitude: Double = 256.11
       val language: String = "en"
       val units: String = "metric"

        // When
        val result = homeViewModel.getWeatherResponse(latitude,longitude,language,units)

        // Then
        assertThat(result , not(nullValue()))
    }

}