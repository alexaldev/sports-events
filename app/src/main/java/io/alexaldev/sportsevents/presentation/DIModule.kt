package io.alexaldev.sportsevents.presentation

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { SportsViewModel(get(), get()) }
}