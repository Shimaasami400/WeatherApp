package com.example.weatherapp.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.FakeLocalSource
import com.example.weatherapp.FakeRemoteSource
import com.example.weatherapp.FakeRepository
import com.example.weatherapp.FavoriteState
import com.example.weatherapp.favorite.viewmodel.FavoriteViewModel
import com.example.weatherapp.model.Current
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherItem
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var favViewModel: FavoriteViewModel
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
    /* val weatherResponse2 = WeatherResponse(
         currentWeather,
         "USA/New York",
         160000,
         emptyList(),
         -73.935242,
         emptyList(),
         40.73061
     )

     val weatherResponse3 = WeatherResponse(
         currentWeather,
         "Japan/Tokyo",
         165000,
         emptyList(),
         139.6917,
         emptyList(),
         35.6895
     )*/

    val favoriteWeather = FavoriteWeather(20,"cairo",12555.22,525.22)

    private  var remoteSource = weatherResponse1
    //private var localSource = listOf(weatherResponse1,weatherResponse2,weatherResponse3)


    @Before
    fun setup() {
        fakeRepository = FakeRepository(FakeRemoteSource(remoteSource), FakeLocalSource())
        favViewModel = FavoriteViewModel(fakeRepository)
    }

    @Test
    fun getFavoriteWeather_Success() = runBlockingTest  {
        // When
        favViewModel.getFavoriteWeather()

        // Then
        val result = favViewModel.favoriteList.first()
        assertThat(result , not(nullValue()))
    }


    @Test
    fun deleteWeatherLocation_deleteItem() = runBlockingTest {

        favViewModel.getFavoriteWeather()

        var initialResult = favViewModel.favoriteList.first()
        assertThat(initialResult, not(nullValue()))

        favViewModel.deleteWeatherLocation(favoriteWeather)

        var afterDeletionResult = favViewModel.favoriteList.first()


        assertThat(afterDeletionResult, equalTo(FavoriteState.Success(emptyList())))
    }
}
