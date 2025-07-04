package com.klivvr.assignment.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.gson.annotations.SerializedName
import androidx.core.net.toUri

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

/**
 * An extension function on the City class to open its location in a map app.
 * @param context The context needed to start the activity.
 */
fun City.openInMap(context: Context) {
    val gmmIntentUri =
        "geo:${this.coordinates.lat},${this.coordinates.lon}?q=${Uri.encode(this.name)}".toUri()

    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")

    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    }
}
