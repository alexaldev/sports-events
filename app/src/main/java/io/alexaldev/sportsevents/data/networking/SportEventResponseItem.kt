package io.alexaldev.sportsevents.data.networking

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SportEventResponseItem(
    @Json(name = "d") val description: String,
    @Json(name = "i") val id: String,
    @Json(name = "sh") val shortDescription: String,
    @Json(name = "si") val sportId: String,
    @Json(name = "tt") val timestamp: Int
)