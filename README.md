# Architecture choices

* Single module approach due to the required features - no need for something extra
* Packages are organized based on the clean data-domain-presentation approach.
* Data and presentation layers can only have access to themselves and domain layer models.
* Presentation layer contains separate model for the ViewItem and necessary mappers from the domain model.
* Similarly, data layer contains specific models for the responses and some core functionality.
* View-based instead of Compose due to my uncertainty with Compose outside pet projects.
* Single `Activity` as a `Fragment` container.
* `RecyclerView` which contains the Sports, and each sport element has another `RecyclerView` which contains the Events.
* `Repository` pattern for the `REST` communication and the Favorites local storage.
* All core components expose mostly `Flows` , so the `SportsViewModel` handles State in a reactive way.
* All available current user actions are modeled as `UserAction` and the `ViewModel` dispatches them.
* A sealed `State` interface describes and contains all the necessary UI states that can occur.
* Unit test just for the
# Features implementation

### Expand-collapse Sport

Each `SportViewItem` has an `expanded` boolean property which defines if the events `RecyclerView` of this sport should be visible or not.
The UserAction of clicking on collapse/expand is passed to the ViewModel.

### Enable favorites filter in a Sport

Each `SportViewItem` has a `filterEnabled` boolean property which defines if the favorites filter is on.
The UserAction of clicking on collapse/expand is passed to the ViewModel. The ViewModel does not filter the events for this sport,
but rather the `SportsAdapter` is responsible to filter the events based on their `isFavorite` property.
This was a choice to keep all the data in the ViewModel for each Sport and let the UI decide for them.

The favorite events ids are kept for the user persistently using `DataStore`. The aggregation of the favorite events with the
events from the REST, is happening in `SportsRepository`.

### Countdown timer for events

A utility `Flow` is emiting in the ViewModel, which is basically a while loop which just emits every x seconds.
This `Flow` is then `combined` with the `Flow` from the repository, so every x second, the `combine` flow gets triggered,
having the latest - always the same - values from the repository and then a mapping happens from domain=>presentation model,
which computes the proper display `remaining` value. This `combined Flow` is collected in the Fragment, which then updates the whole RecyclerView with
the new values.

### Libraries

- Retrofit + Moshi for REST communication
- Koin for Dependency injection
- DataStore for easier `Flow` exposure from SharedPreferences
- AssertK for a bit easier assertions
- mockk + turbine for mocking and testing Flows
- viewBindingDelegate a utility to not bother with the lifecycle of the viewBinding in the Fragment
- Utility `Result` class (domain/Result): Basically a more convinient wrapper of Kotlin's `Result` to wrap any possible REST error

# Improvements

- No available Android emulator found with SDK 21 :(, tested against emulators with Android 16-Android 7.
- UI test with Kaspresso to test the expand-collapse, filter toggling features.
- Unit test the `DefaultSportsRepository`, mostly the combination of the REST and favorites data sources.
- Moving the mapping of ViewItems to a separate thread.
- Refactor-cleanup the `onUserAction` function in ViewModel, can be smaller
- Any kind of optimization in the update of the countdown timer related to the UI, maybe some optimization in the binding function.