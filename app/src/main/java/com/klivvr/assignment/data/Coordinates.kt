package com.klivvr.assignment.data

import com.google.gson.annotations.SerializedName

data class Coordinates(
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("lat")
    val lat: Double
)
