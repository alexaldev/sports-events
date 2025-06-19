package io.alexaldev.sportsevents.domain

import kotlinx.coroutines.flow.Flow

interface SportsRepository {
    fun observeSports(): Flow<Result<List<Sport>, DataError>>
    fun getSports()
    suspend fun toggleEventFavorite(eventId: String)
}