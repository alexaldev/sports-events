package io.alexaldev.sportsevents.data.networking

import io.alexaldev.sportsevents.domain.Event
import io.alexaldev.sportsevents.domain.Sport

fun SportEventResponseItem.toDomain(): Event {
    val (firstCompetitor, secondCompetitor) = this.description.split("-")
    return Event(
        id = this.id,
        firstCompetitor = firstCompetitor,
        secondCompetitor = secondCompetitor,
        sportId = this.sportId,
        startTime = this.timestamp,
        isFavorite = false
    )
}

fun SportResponseItem.toDomain(): Sport {
    return Sport(
        id = this.id,
        name = this.sportName,
        activeEvents = this.events.map(SportEventResponseItem::toDomain)
    )
}