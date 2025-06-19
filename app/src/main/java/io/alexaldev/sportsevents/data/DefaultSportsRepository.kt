package io.alexaldev.sportsevents.data

import io.alexaldev.sportsevents.data.networking.SportResponseItem
import io.alexaldev.sportsevents.data.networking.SportsRemoteService
import io.alexaldev.sportsevents.data.networking.toDomain
import io.alexaldev.sportsevents.domain.DataError
import io.alexaldev.sportsevents.domain.Result
import io.alexaldev.sportsevents.domain.Sport
import io.alexaldev.sportsevents.domain.SportsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DefaultSportsRepository(
    private val sportsRemoteService: SportsRemoteService,
    private val favoritesDataSource: FavoritesDataSource,
    private val ioScope: CoroutineScope
) : SportsRepository {

    private val sportsStateFlow = MutableStateFlow<Result<List<Sport>, DataError>>(Result.Loading)

    override fun observeSports(): Flow<Result<List<Sport>, DataError>> {

        return combine(
            this.sportsStateFlow,
            favoritesDataSource.favoriteFlow
        ) { sportsResult, favorites ->
            when (sportsResult) {
                is Result.Success -> {
                    val updated = sportsResult.data.map { sport ->
                        sport.copy(
                            activeEvents = sport.activeEvents.map { event ->
                                event.copy(isFavorite = event.id in favorites)
                            }
                        )
                    }
                    Result.Success(updated)
                }

                else -> sportsResult
            }
        }
    }

    override fun getSports() {

        ioScope.launch {

            val currentFavorites = favoritesDataSource.getAll()

            val sports = safeCall { sportsRemoteService.getAllSports() }
            when (sports) {
                is Result.Error -> sportsStateFlow.update { Result.Error(sports.error) }
                Result.Loading -> sportsStateFlow.update { Result.Loading }
                is Result.Success -> {
                    val result = sports.data.map(SportResponseItem::toDomain)
                        .map { sport ->
                            sport.copy(
                                activeEvents = sport.activeEvents.map { event ->
                                    if (event.id in currentFavorites) event.copy(isFavorite = true) else event
                                }
                            )
                        }
                    sportsStateFlow.update { Result.Success(result) }
                }
            }
        }

    }

    override suspend fun toggleEventFavorite(eventId: String) {
        favoritesDataSource.favoriteToggled(eventId)
    }
}