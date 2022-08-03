package me.siddheshkothadi.autofism3.network

import kotlinx.coroutines.flow.Flow
import me.siddheshkothadi.autofism3.model.Fish
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileAPI {

    @Multipart
    @POST("/api/upload")
    suspend fun uploadData(
        @Part image: MultipartBody.Part,
        @Part("longitude") longitude: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
    )

    @GET("/api/history")
    suspend fun getHistory() : List<Fish>
}