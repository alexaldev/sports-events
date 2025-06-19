package io.alexaldev.sportsevents.data.networking

import retrofit2.Response
import retrofit2.http.GET

interface SportsRemoteService {

    @GET("MockSports/sports.json")
    suspend fun getAllSports(): Response<List<SportResponseItem>>
}