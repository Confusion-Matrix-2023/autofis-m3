package me.siddheshkothadi.autofism3.network

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
        @Part("device_id") deviceId: RequestBody,
        @Part("submission_timestamp") subTimeStamp: RequestBody,
    ): SubmitResponse
}