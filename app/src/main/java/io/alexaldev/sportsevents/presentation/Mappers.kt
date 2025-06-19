package io.alexaldev.sportsevents.presentation

import io.alexaldev.sportsevents.domain.Event
import io.alexaldev.sportsevents.domain.Sport

fun Event.toViewItem(): EventViewItem {
    return EventViewItem(
        id = this.id,
        remaining = unixTimeToPresentable(
            unixTime = this.startTime,
            currentUnixTime = (System.currentTimeMillis() / 1000).toInt()
        ),
        firstCompetitor = this.firstCompetitor,
        secondCompetitor = this.secondCompetitor,
        isFavorite = this.isFavorite
    )
}

fun Sport.toViewItem(
    isExpanded: Boolean = true,
    isFilterEnabled: Boolean = false
): SportViewItem {
    return SportViewItem(
        id = this.id,
        title = this.name,
        isFilterEnabled = isFilterEnabled,
        isExpanded = isExpanded,
        events = this.activeEvents.map { it.toViewItem() }
    )
}