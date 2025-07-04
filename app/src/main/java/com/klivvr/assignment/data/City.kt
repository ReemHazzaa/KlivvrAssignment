package com.klivvr.assignment.data

import com.google.gson.annotations.SerializedName

/**
 * Represents the structure of a single city entry in the JSON file.
 */
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
