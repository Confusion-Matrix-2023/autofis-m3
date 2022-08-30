package me.siddheshkothadi.autofism3.network

import com.google.gson.annotations.SerializedName
import me.siddheshkothadi.autofism3.model.Recognition

data class HistoryBlock(
    @SerializedName("recogintion") val recogintion: List<Recognition>?,
    @SerializedName("submissions") val submissions: List<Recognition>?,
)
