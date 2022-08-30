package me.siddheshkothadi.autofism3.network

import com.google.gson.annotations.SerializedName

data class HistoryResponse(
    @SerializedName("data") val data: List<HistoryBlock>
)
