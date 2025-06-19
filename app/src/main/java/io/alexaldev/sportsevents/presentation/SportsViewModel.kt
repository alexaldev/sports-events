package io.alexaldev.sportsevents.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.alexaldev.sportsevents.domain.InternetConnectivityChecker
import io.alexaldev.sportsevents.domain.Result
import io.alexaldev.sportsevents.domain.SportsRepository
import io.alexaldev.sportsevents.presentation.ScreenState.Sports
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SportsViewModel(
    private val sportsRepository: SportsRepository,
    private val internetConnectivityChecker: InternetConnectivityChecker,
    private val countdownTick: Duration = 1.seconds,
    private val ticksEnabled: Boolean = true
) : ViewModel() {

    private val tickerFlow = flow {

        if (!ticksEnabled) {
            emit(Unit)
            return@flow
        }

        while (coroutineContext.isActive) {
            emit(Unit)
            delay(countdownTick)
        }
    }

    private val currentSports = hashMapOf<String, SportViewItem>()

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    fun fetchSports() {

        if (internetConnectivityChecker.isNotConnected()) {
            _screenState.value = ScreenState.NoInternet
            return
        }

        viewModelScope.launch {

            combine(
                sportsRepository.observeSports(),
                tickerFlow
            ) { sportsResult, _ ->
                when (sportsResult) {
                    is Result.Error -> ScreenState.Error("Error: ${sportsResult.error}")
                    is Result.Success -> {

                        val updatedSports = sportsResult.data.map { sport ->
                            currentSports[sport.id]?.let { currentSport ->

                                sport.toViewItem(
                                    isExpanded = currentSport.isExpanded,
                                    isFilterEnabled = currentSport.isFilterEnabled
                                )
                            } ?: sport.toViewItem()
                        }

                        Sports(updatedSports)
                    }

                    Result.Loading -> ScreenState.Loading
                }
            }.collect { screenState ->
                if (screenState is Sports) {
                    currentSports.clear()
                    currentSports.putAll(screenState.sportsViewItems.associateBy { it.id })
                }
                _screenState.value = screenState
            }
        }

        sportsRepository.getSports()
    }

    fun onAction(userAction: UserAction) {
        // User Actions can be triggered only in Sports state
        if (_screenState.value !is Sports) {
            // Should not happen
            _screenState.value = ScreenState.Error("An error occurred :(")
            return
        }
        when (userAction) {
            is UserAction.EventFavorited -> {
                val sports = (_screenState.value as Sports).sportsViewItems

                val updatedSports = sports.map { sport ->
                    if (sport.events.any { it.id == userAction.event.id }) {
                        val updatedEvents = sport.events.map { event ->
                            if (event.id == userAction.event.id) {
                                viewModelScope.launch { sportsRepository.toggleEventFavorite(event.id) }
                                event.copy(isFavorite = !event.isFavorite)
                            } else event
                        }
                        sport.copy(events = updatedEvents)
                    } else {
                        sport
                    }
                }
                currentSports.clear()
                currentSports.putAll(updatedSports.associateBy { it.id })

                _screenState.value = Sports(updatedSports)
            }

            is UserAction.SportCollapsed -> {
                val sports = (_screenState.value as Sports).sportsViewItems

                val updatedSports = sports.map { sport ->
                    if (sport.id == userAction.sport.id) sport.copy(isExpanded = false)
                    else sport
                }

                currentSports.clear()
                currentSports.putAll(updatedSports.associateBy { it.id })
                _screenState.value = Sports(updatedSports)
            }

            is UserAction.SportExpanded -> {
                val sports = (_screenState.value as Sports).sportsViewItems

                val updatedSports = sports.map { sport ->
                    if (sport.id == userAction.sport.id) sport.copy(isExpanded = true)
                    else sport
                }

                currentSports.clear()
                currentSports.putAll(updatedSports.associateBy { it.id })
                _screenState.value = Sports(updatedSports)
            }

            is UserAction.SportFilterToggled -> {
                val sports = (_screenState.value as Sports).sportsViewItems

                val updatedSports = sports.map { sport ->
                    if (sport.id == userAction.sport.id) sport.copy(isFilterEnabled = userAction.filterEnabled)
                    else sport
                }

                currentSports.clear()
                currentSports.putAll(updatedSports.associateBy { it.id })
                _screenState.value = Sports(updatedSports)
            }
        }
    }
}

sealed interface ScreenState {
    data object Loading : ScreenState
    data object NoInternet : ScreenState
    data class Error(val message: String = "") : ScreenState
    data class Sports(val sportsViewItems: List<SportViewItem>) : ScreenState
}

sealed interface UserAction {
    data class EventFavorited(val event: EventViewItem) : UserAction
    data class SportExpanded(val sport: SportViewItem) : UserAction
    data class SportCollapsed(val sport: SportViewItem) : UserAction
    data class SportFilterToggled(val sport: SportViewItem, val filterEnabled: Boolean) : UserAction
}