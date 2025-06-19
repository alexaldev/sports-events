package io.alexaldev.sportsevents.presentation

data class SportViewItem(
    val id: String = "",
    val title: String = "",
    val isFilterEnabled: Boolean = false,
    val isExpanded: Boolean = false,
    val events: List<EventViewItem> = emptyList()
)