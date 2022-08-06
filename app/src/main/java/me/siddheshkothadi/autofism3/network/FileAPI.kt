package me.siddheshkothadi.autofism3.network

import me.siddheshkothadi.autofism3.model.UploadHistoryFish
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
    )

    @GET("/api/history")
    suspend fun getHistory(
        @Header("Authorization") bearerToken: String
    ) : List<UploadHistoryFish>
}