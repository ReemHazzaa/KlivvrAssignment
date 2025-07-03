package com.klivvr.assignment.data

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("country")
    val country: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("_id")
    val id: Int,
    @SerializedName("coord")
    val coordinates: Coordinates
)
