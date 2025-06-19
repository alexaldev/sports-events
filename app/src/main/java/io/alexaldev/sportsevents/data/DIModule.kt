package io.alexaldev.sportsevents.data

import com.squareup.moshi.Moshi
import io.alexaldev.sportsevents.data.networking.SportsRemoteService
import io.alexaldev.sportsevents.domain.InternetConnectivityChecker
import io.alexaldev.sportsevents.domain.SportsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val dataModule = module {
    singleOf(::FavoritesDataSource)
    single<SportsRepository>(named("fakeRepository")) { FakeSportsRepository() }
    single<SportsRepository> { get(named("default")) }
    single<SportsRepository>(named("default")) {
        DefaultSportsRepository(
            sportsRemoteService = get(),
            favoritesDataSource = get(),
            ioScope = CoroutineScope(Dispatchers.IO)
        )
    }
    singleOf(::AndroidConnectivityChecker) bind InternetConnectivityChecker::class

    single<Retrofit> {

        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        val moshi = Moshi.Builder().build()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl("https://example.com")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    single<SportsRemoteService> {
        get<Retrofit>().create(SportsRemoteService::class.java)
    }
}