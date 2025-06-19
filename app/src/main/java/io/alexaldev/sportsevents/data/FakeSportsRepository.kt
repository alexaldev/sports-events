package io.alexaldev.sportsevents.data

import io.alexaldev.sportsevents.domain.DataError
import io.alexaldev.sportsevents.domain.Event
import io.alexaldev.sportsevents.domain.Result
import io.alexaldev.sportsevents.domain.Sport
import io.alexaldev.sportsevents.domain.SportsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeSportsRepository : SportsRepository {

    companion object {
        fun fakeSport1() = Sport(
            id = "FOOT",
            name = "Soccer",
            activeEvents = listOf(
                Event(
                    id = "45522851",
                    firstCompetitor = "SpainU19",
                    secondCompetitor = "NorwayU19",
                    isFavorite = false,
                    sportId = "FOOT",
                    startTime = 1750435200
                ),
                Event(
                    id = "45522852",
                    firstCompetitor = "GreeceU19",
                    secondCompetitor = "NorwayU19",
                    isFavorite = true,
                    sportId = "FOOT",
                    startTime = 1750176000
                ),
                Event(
                    id = "45522853",
                    firstCompetitor = "Spain",
                    secondCompetitor = "Norway",
                    isFavorite = false,
                    sportId = "FOOT",
                    startTime = 1750176000
                ),
                Event(
                    id = "45522850",
                    firstCompetitor = "GreeceU19",
                    secondCompetitor = "NorwayU19",
                    isFavorite = true,
                    sportId = "FOOT",
                    startTime = 1750176000
                ),
                Event(
                    id = "45522856",
                    firstCompetitor = "GreeceU19",
                    secondCompetitor = "NorwayU19",
                    isFavorite = true,
                    sportId = "FOOT",
                    startTime = 1657944684
                ),
            ),
        )

        fun fakeSport2() = Sport(
            id = "BASK",
            name = "Basketball",
            activeEvents = listOf(
                Event(
                    id = "455221",
                    firstCompetitor = "Bask1",
                    secondCompetitor = "Bask2",
                    isFavorite = false,
                    sportId = "FOOT",
                    startTime = 1750435200
                ),
            )
        )
    }

    override fun observeSports(): Flow<Result<List<Sport>, DataError>> {
        return flowOf(

            Result.Success(
                listOf(
                    fakeSport1(), fakeSport2()
                )
            )
        )
    }

    override fun getSports() {
        TODO("Not yet implemented")
    }

    override suspend fun toggleEventFavorite(eventId: String) {
        TODO("Not yet implemented")
    }
}