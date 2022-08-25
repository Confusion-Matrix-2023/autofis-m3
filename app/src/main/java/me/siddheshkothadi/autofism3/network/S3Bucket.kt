package me.siddheshkothadi.autofism3.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Url

interface S3Bucket {
    @PUT
    suspend fun uploadImage(
        @Url url: String,
        @Header("Content-Type") contentType: String,
        @Body body: RequestBody
    ): ResponseBody
}