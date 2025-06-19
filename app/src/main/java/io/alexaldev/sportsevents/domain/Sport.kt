package io.alexaldev.sportsevents.domain

data class Sport(
    val id: String,
    val name: String,
    val activeEvents: List<Event>
)