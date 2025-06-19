package io.alexaldev.sportsevents.domain

data class Event(
    val id: String,
    val firstCompetitor: String,
    val secondCompetitor: String,
    val isFavorite: Boolean,
    val sportId: String,
    val startTime: Int
)