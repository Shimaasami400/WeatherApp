package com.example.weatherapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.model.AlertPojo
import com.example.weatherapp.model.Current
import com.example.weatherapp.model.FavoriteWeather
import com.example.weatherapp.model.WeatherItem
import com.example.weatherapp.model.WeatherRepositoryImp
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {

    private lateinit var weatherRepository: WeatherRepositoryImp
    private lateinit var mockRemoteDataSource: FakeRemoteSource
    private lateinit var mockLocalDataSource: FakeLocalSource

    val currentWeather = Current(
        1000076746,
        50.5,
        10000,
        5.0.toFloat(),
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

    var weatherResponse = WeatherResponse(
        currentWeather,
        "Egypt/cairo",
        155000,
        emptyList(),
        -74.006,
        emptyList(),
        40.7128,
    )

    @Before
    fun setup() {
        mockRemoteDataSource = FakeRemoteSource(weatherResponse)
        mockLocalDataSource = FakeLocalSource()
        weatherRepository = WeatherRepositoryImp.getInstance(mockRemoteDataSource, mockLocalDataSource)
    }

    @Test
    fun getAllWeather_longitudeAndLatitude_weatherResponse() {
        val result = runBlocking { weatherRepository.getWeatherForecast(0.0, 0.0, "metric", "en").firstOrNull() }
        assertNotNull(result)
        assertEquals(weatherResponse, result)
    }

     @Test
     fun insertWeather_favoriteWeatherLocation_insertedLocation() = runBlocking {
         val favoriteWeather = FavoriteWeather(
              100,
             "cairo" ,
              10.11111,
              -100.55
         )
         weatherRepository.insertWeather(favoriteWeather)

         val allFavorites = weatherRepository.getAllWeather().first()
         //assertTrue(allFavorites.contains(favoriteWeather))
         assertThat( allFavorites.contains(favoriteWeather), `is`(true))

     }

    @Test
    fun deleteWeather_favoriteWeatherLocation() = runBlockingTest {
        val favoriteWeather2 = FavoriteWeather(
            200,
            "cairo" ,
            10.11111,
            -100.55
        )
        weatherRepository.insertWeather(favoriteWeather2)
        weatherRepository.deleteWeather(favoriteWeather2)

        val allFavorites = weatherRepository.getAllWeather().first()
        assertThat( allFavorites.contains(favoriteWeather2), `is`(false))

    }

    /*@Test
    fun getAllAlerts_longitudeAndLatitude_weatherResponse() {
        val result = runBlocking { weatherRepository.getWeatherForecast(0.0, 0.0, "metric", "en").firstOrNull() }
        assertNotNull(result)
        assertEquals(weatherResponse, result)
    }*/

    @Test
    fun insertAlert_alertInserted() = runBlockingTest {
        val alert = AlertPojo(
             "20",
            122.2,
            222.2,
            -555,
            5222,
             "ALARM",

        )
        weatherRepository.insertAlert(alert)

        val allAlerts = weatherRepository.getAllAlerts().first()
        assertThat(allAlerts.contains(alert), `is`(true))
    }

    @Test
    fun deleteAlert_alertDeleted() = runBlockingTest {
        val alert2 = AlertPojo(
            "30",
            12.22,
            2222.2,
            -555,
            5222,
            "ALARM",
        )
        weatherRepository.insertAlert(alert2)
        weatherRepository.deleteAlert(alert2)

        val allAlerts = weatherRepository.getAllAlerts().first()
        assertThat(allAlerts.contains(alert2), `is`(false))
    }

}
