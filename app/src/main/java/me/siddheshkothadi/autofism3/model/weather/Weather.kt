package me.siddheshkothadi.autofism3.model.weather

import com.google.gson.annotations.SerializedName


data class Weather(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("main") var main: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("icon") var icon: String? = null

)