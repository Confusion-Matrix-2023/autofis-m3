package me.siddheshkothadi.autofism3.network

import me.siddheshkothadi.autofism3.model.UploadHistoryFish
import me.siddheshkothadi.autofism3.model.weather.Weather
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface FileAPI {

    @Multipart
    @POST("/api/upload")
    suspend fun uploadData(
        @Header("Authorization") bearerToken: String,
        @Part image: MultipartBody.Part,
        @Part("longitude") longitude: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part("temp") temp: RequestBody?,
        @Part("pressure") pressure: RequestBody?,
        @Part("humidity") humidity: RequestBody?,
        @Part("speed") speed: RequestBody?,
        @Part("deg") deg: RequestBody?,
    )

    @POST("/api/weather")
    suspend fun getWeather(
        @Body locationData: LocationData
    ): Weather

    @GET("/api/history")
    suspend fun getHistory(
        @Header("Authorization") bearerToken: String
    ) : List<UploadHistoryFish>
}