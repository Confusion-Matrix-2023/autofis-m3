package me.siddheshkothadi.autofism3.network

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface WeatherAPI {
    @GET()
    suspend fun getWeatherData(
        @Url url: String
    ): JsonObject
}