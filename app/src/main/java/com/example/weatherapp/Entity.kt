package com.example.weatherapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_data")
data class WeatherData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val cityName: String,
    val description: String,
    val tempMax: Int,
    val tempMin: Int
)
