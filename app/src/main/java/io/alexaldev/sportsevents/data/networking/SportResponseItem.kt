package io.alexaldev.sportsevents.data.networking

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SportResponseItem(
    @Json(name = "i") val id: String,
    @Json(name = "d") val sportName: String,
    @Json(name = "e") val events: List<SportEventResponseItem>
)