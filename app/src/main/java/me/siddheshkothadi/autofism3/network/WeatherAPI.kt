package me.siddheshkothadi.autofism3.network

import me.siddheshkothadi.autofism3.model.weather.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("/data/2.5/weather?")
    suspend fun getWeatherData(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") apiKey: String
    ): Weather
}