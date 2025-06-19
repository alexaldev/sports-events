package io.alexaldev.sportsevents.domain

import io.alexaldev.sportsevents.domain.Result.Success

sealed interface Result<out D, out E : Error> {
    data class Success<out D>(
        val data: D,
    ) : Result<D, Nothing>

    data class Error<out E : io.alexaldev.sportsevents.domain.Error>(
        val error: E,
    ) : Result<Nothing, E>

    data object Loading : Result<Nothing, Nothing>
}

inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> =
    when (this) {
        is Result.Error -> Result.Error(error)
        is Success -> Success(map(data))
        Result.Loading -> Result.Loading
    }

fun <T, E : Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> = map { }

typealias EmptyResult<E> = Result<Unit, E>
