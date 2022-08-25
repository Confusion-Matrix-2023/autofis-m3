package me.siddheshkothadi.autofism3.model.weather

import com.google.gson.annotations.SerializedName


data class Clouds (

  @SerializedName("all" ) var all : Int? = null

)