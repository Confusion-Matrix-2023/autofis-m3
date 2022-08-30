package me.siddheshkothadi.autofism3.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.siddheshkothadi.autofism3.model.UploadHistoryFish
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface AWSFileAPI {
    @POST("/api/addNewDevice")
    suspend fun addNewDevice(
        @Body addDeviceRequest: AddDeviceRequest
    ): AuthResponse

    @GET("/api/checkDevice")
    suspend fun checkDevice(
        @Header("Authorization") bearerToken: String
    ): AuthResponse

    @POST("/api/testsubmission")
    suspend fun submitDetails(
        @Header("Authorization") bearerToken: String,
        @Body body: SubmitBody
    ): SubmitResponse

    @Multipart
    @POST("/api/submission")
    suspend fun submitDetailsWithImage(
        @Header("Authorization") bearerToken: String,
        @Part image: MultipartBody.Part,
        @Part("longitude") longitude: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("device_id") deviceId: RequestBody,
        @Part("submission_timestamp") subTimeStamp: RequestBody,
        @Part("temperature") temp: RequestBody?,
        @Part("pressure") pressure: RequestBody?,
        @Part("humidity") humidity: RequestBody?,
        @Part("wind_speed") speed: RequestBody?,
        @Part("wind_direction") deg: RequestBody?,
    ): JsonObject

    @GET("/api/userSubmission")
    suspend fun getUploadHistory(
        @Header("Authorization") bearerToken: String
    ): HistoryResponse
}