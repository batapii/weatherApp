package com.example.weatherapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

    @Dao
    interface WeatherDao {
        @Query("SELECT * FROM weather_data")
        fun getAllWeatherData(): List<WeatherData>

        @Insert
        fun insert(weatherData: WeatherData)

        @Delete
        fun delete(weatherData: WeatherData)
    }