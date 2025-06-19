package io.alexaldev.sportsevents.presentation

data class EventViewItem(
    val id: String = "",
    val remaining: String = "",
    val firstCompetitor: String = "",
    val secondCompetitor: String = "",
    val isFavorite: Boolean = false
)