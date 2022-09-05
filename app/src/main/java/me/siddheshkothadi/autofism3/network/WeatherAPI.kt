package me.siddheshkothadi.autofism3.network

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface WeatherAPI {
    @GET("data/2.5/weather?lat=12&lon=56&appid=2851a90b716da669a9118af4c2b59341")
    suspend fun getWeatherData(
//        @Query("lat") latitude: String,
//        @Query("lon") longitude: String,
//        @Query("appid") apiKey: String = "2851a90b716da669a9118af4c2b59341"
    ): JsonObject

    @GET()
    suspend fun getWeatherData(
        @Url url: String
    ): JsonObject
}