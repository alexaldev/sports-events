package io.alexaldev.sportsevents.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import io.alexaldev.sportsevents.data.FakeSportsRepository
import io.alexaldev.sportsevents.domain.DataError
import io.alexaldev.sportsevents.domain.Event
import io.alexaldev.sportsevents.domain.InternetConnectivityChecker
import io.alexaldev.sportsevents.domain.Result
import io.alexaldev.sportsevents.domain.Sport
import io.alexaldev.sportsevents.domain.SportsRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class SportsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: SportsRepository
    private lateinit var testViewModel: SportsViewModel
    private lateinit var mockInternetConnectivityChecker: InternetConnectivityChecker

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        mockInternetConnectivityChecker = mockk()
        every { mockRepository.getSports() } just Runs
        coEvery { mockRepository.toggleEventFavorite(any()) } just Runs
        every { mockInternetConnectivityChecker.isNotConnected() } returns false
        testViewModel =
            SportsViewModel(
                mockRepository,
                mockInternetConnectivityChecker,
                ticksEnabled = false
            )
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when no device internet connection, requesting sports emits NoInternet state`() = runTest {
        every { mockInternetConnectivityChecker.isNotConnected() } returns true

        testViewModel.screenState.test {
            // Check-skip initial
            assertThat(awaitItem()).isEqualTo(ScreenState.Loading)

            testViewModel.fetchSports()
            advanceUntilIdle()

            val screenState = awaitItem()
            assertThat(screenState).isEqualTo(ScreenState.NoInternet)
        }
    }

    @Test
    fun `when server error occurs, requesting sports emits Error with a message`() = runTest {

        every { mockRepository.observeSports() } returns flowOf(Result.Error(DataError.Network.SERVER_ERROR))

        testViewModel.screenState.test {
            // Skip initial
            awaitItem()

            testViewModel.fetchSports()
            advanceUntilIdle()

            val screenState = awaitItem()
            assertThat(screenState).isInstanceOf(ScreenState.Error::class)
        }
    }

    @Test
    fun `on favorite event user action, the proper view item gets updated`() = runTest {

        val fakeSport = FakeSportsRepository.fakeSport1()
        val fakeSportViewItem = fakeSport.toViewItem()
        val fakeEventToFavorite = fakeSportViewItem.events.first()
        every { mockRepository.observeSports() } returns flowOf(Result.Success(listOf(fakeSport)))

        testViewModel.screenState.test {
            // Skip initial Loading
            awaitItem()
            testViewModel.fetchSports()
            advanceUntilIdle()
            testViewModel.onAction(UserAction.EventFavorited(fakeEventToFavorite))
            awaitItem() // Skip the updated Sports value
            val state = awaitItem()
            assertThat(state).isInstanceOf(ScreenState.Sports::class)
            val vmSports = (state as ScreenState.Sports).sportsViewItems
            assertThat(vmSports.first().events.first { it.id == fakeEventToFavorite.id }.isFavorite).isTrue()
        }
    }

    @Test
    fun `on collapse sport user action, the proper viewItem gets updated`() = runTest {

        val fakeSport = FakeSportsRepository.fakeSport1()
        val fakeSportViewItem = fakeSport.toViewItem()
        every { mockRepository.observeSports() } returns flowOf(Result.Success(listOf(fakeSport)))

        testViewModel.screenState.test {
            // Skip initial Loading
            awaitItem()
            testViewModel.fetchSports()
            advanceUntilIdle()
            testViewModel.onAction(UserAction.SportCollapsed(fakeSportViewItem))
            awaitItem() // Skip the updated Sports value

            val state = awaitItem()

            assertThat(state).isInstanceOf(ScreenState.Sports::class)
            val vmSports = (state as ScreenState.Sports).sportsViewItems
            assertThat(vmSports.first().isExpanded).isFalse()
        }
    }

    @Test
    fun `on filterToggle sport user action, the proper viewItem gets updated`() = runTest {

        val fakeSport = FakeSportsRepository.fakeSport1()
        val fakeSportViewItem = fakeSport.toViewItem()
        every { mockRepository.observeSports() } returns flowOf(Result.Success(listOf(fakeSport)))

        testViewModel.screenState.test {
            // Skip initial Loading
            awaitItem()
            testViewModel.fetchSports()
            advanceUntilIdle()
            testViewModel.onAction(
                UserAction.SportFilterToggled(
                    fakeSportViewItem,
                    filterEnabled = true
                )
            )
            awaitItem() // Skip the updated Sports value

            val state = awaitItem()

            assertThat(state).isInstanceOf(ScreenState.Sports::class)
            val vmSports = (state as ScreenState.Sports).sportsViewItems
            assertThat(vmSports.first().isFilterEnabled).isTrue()

//            cancelAndIgnoreRemainingEvents()
        }
    }

    //    @Test()
    fun `on every desired tick, the events view items have their remaining value decreased`() =
        runTest {
            val testDispatcher = StandardTestDispatcher(testScheduler)
            val fakeNow = System.currentTimeMillis() / 1000
            val fakeFutureEventStartTime = 5.seconds
            val fakeStartTime = fakeNow + 5
            val fakeEvent = Event(
                id = "ignore",
                firstCompetitor = "",
                secondCompetitor = "",
                isFavorite = false,
                startTime = fakeStartTime.toInt(),
                sportId = "ignore"
            )
            val fakeSport = Sport(id = "ignore", name = "ignore", activeEvents = listOf(fakeEvent))
            every { mockRepository.observeSports() } returns flowOf(Result.Success(listOf(fakeSport)))
            val fakeTick = 1.seconds
            val testViewModel = SportsViewModel(
                mockRepository,
                mockInternetConnectivityChecker,
                countdownTick = fakeTick,
                ticksEnabled = true,
            )

            testViewModel.screenState.test {
                // Skip initial Loading
                awaitItem()
                testViewModel.fetchSports()
                advanceTimeBy(fakeTick)
                advanceUntilIdle()
                val state = awaitItem()
                assertThat(state).isInstanceOf(ScreenState.Sports::class)
                val vmSports = (state as ScreenState.Sports).sportsViewItems

                val futureMinusFakeTick =
                    fakeNow + (fakeFutureEventStartTime - fakeTick).inWholeSeconds
                assertThat(vmSports.first().events.first().remaining)
                    .isEqualTo(
                        unixTimeToPresentable(
                            unixTime = futureMinusFakeTick.toInt(),
                            currentUnixTime = fakeNow.toInt()
                        )
                    )

                testDispatcher.cancel()
                cancelAndIgnoreRemainingEvents()
                cancel()
            }
        }
}