package io.alexaldev.sportsevents.presentation

fun unixTimeToPresentable(unixTime: Int, currentUnixTime: Int): String {
    val remainingSeconds = (unixTime - currentUnixTime).coerceAtLeast(0)

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
