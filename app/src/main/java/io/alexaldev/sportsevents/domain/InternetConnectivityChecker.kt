package io.alexaldev.sportsevents.domain

interface InternetConnectivityChecker {
    fun isConnected(): Boolean
    fun isNotConnected(): Boolean
}