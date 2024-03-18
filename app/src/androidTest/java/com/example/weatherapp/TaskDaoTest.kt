package com.example.weatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.db.AppDatabase
import com.example.weatherapp.model.FavoriteWeather
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TaskDaoTest {

    @get:Rule
    var instanceExcutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    @Before
    fun setUp(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun end(){
        database.close()
    }

    @Test
    fun getFavoriteWeather_favoriteWeatherLocation_gettingTheSavedLocation() =
        runBlockingTest {
            val favorite = FavoriteWeather(1,"cairo",0.0,0.0)
            database.weatherDataBaseDao().insertWeather(favorite)

            val result = database.weatherDataBaseDao().getFavoriteWeather().first()

            assertThat<List<FavoriteWeather>>(result as List<FavoriteWeather>,
                CoreMatchers.notNullValue()
            )
            assertThat(result[0].roomId, Matchers.`is`(favorite.roomId))
        }

    @Test
    fun deleteWeather_favoriteWeatherLocation_removeTheSavedLocation() = runBlockingTest {
        val favorite = FavoriteWeather(1,"cairo",0.0,0.0)
        database.weatherDataBaseDao().deleteWeather(favorite)

        val result = database.weatherDataBaseDao().getFavoriteWeather().first()
        assertThat(result.isEmpty(), Matchers.`is`(true))
    }

}