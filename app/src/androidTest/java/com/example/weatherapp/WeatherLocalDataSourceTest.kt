package com.example.weatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.db.AppDatabase
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.db.WeatherLocalDataSourceImp
import com.example.weatherapp.model.FavoriteWeather
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherLocalDataSourceTest {

    @get:Rule
    var instanceExcutorRule = InstantTaskExecutorRule()

    private lateinit var weatherLocalDataSource: WeatherLocalDataSource
    private lateinit var database: AppDatabase

    @Before
    fun setUp(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        weatherLocalDataSource = WeatherLocalDataSourceImp(database.weatherDataBaseDao())
    }

    @After
    fun end(){
        database.close()
    }

    @Test
    fun insertLocation() = runBlockingTest{
        val location = FavoriteWeather(1,"cairo",0.0,0.0)

        database.weatherDataBaseDao().insertWeather(location)

        val result = weatherLocalDataSource.getAllWeather().first()

        MatcherAssert.assertThat<List<FavoriteWeather>>(
            result as List<FavoriteWeather>,
            CoreMatchers.notNullValue()
        )
        MatcherAssert.assertThat(result[0].roomId, Matchers.`is`(location.roomId))
    }

    @Test
    fun deleteLocation() = runBlockingTest {
        val location = FavoriteWeather(1,"cairo",0.0,0.0)
        weatherLocalDataSource.deleteWeather(location)

        val result = weatherLocalDataSource.getAllWeather().first()
        MatcherAssert.assertThat(result.isEmpty(), Matchers.`is`(true))
    }


}