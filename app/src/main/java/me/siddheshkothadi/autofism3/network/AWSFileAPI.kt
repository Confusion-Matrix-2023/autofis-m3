package me.siddheshkothadi.autofism3.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AWSFileAPI {
    @POST("/api/addNewDevice")
    suspend fun addNewDevice(
        deviceName: String,
        deviceKey: String
    ): AuthResponse

    @GET("/api/checkDevice")
    suspend fun checkDevice(
        @Header("Authorization") bearerToken: String
    ): AuthResponse

    @POST("/api/submission")
    suspend fun submitDetails(
        @Header("Authorization") bearerToken: String,
        @Body body: SubmitDetailsRequest
    )
}