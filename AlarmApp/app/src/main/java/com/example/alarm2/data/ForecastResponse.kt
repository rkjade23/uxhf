package com.example.alarm2.data

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: ForecastMain,
    val weather: List<ForecastWeather>,
    val clouds: Clouds?, // Make clouds nullable
    val wind: ForecastWind?, // Make wind nullable
    val visibility: Int,
    val pop: Double,
    val sys: ForecastSys,
    val dt_txt: String,
    val rain: Rain?, // Make rain nullable
    val snow: Snow? // Make snow nullable
)

data class ForecastMain(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int?,
    val grnd_level: Int?,
    val humidity: Int,
    val temp_kf: Double
)

data class ForecastWeather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class ForecastWind(
    val speed: Double,
    val deg: Int,
    val gust: Double?
)

data class ForecastSys(
    val pod: String
)

data class Rain(
    @field:SerializedName("3h") val threeHour: Double?
)

data class Snow(
    @field:SerializedName("3h") val threeHour: Double?
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Int,
    val sunset: Int
)
