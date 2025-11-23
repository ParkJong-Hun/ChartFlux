package example.advanced

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import parkjonghun.github.io.chartflux.ChartStore

/**
 * Advanced example: Using sealed classes and nullable types with ChartStore
 *
 * This demonstrates:
 * - Sealed class state values (LoadingState)
 * - Nullable state values (lastError)
 * - Pattern matching on sealed classes
 */
fun main() = runBlocking {
    // 1. Create initial state
    val initialState = UserState(
        userData = UserDataProperty(LoadingState.Loading),
        lastError = LastErrorProperty(null)
    )

    // 2. Create ChartStore instance
    val chartStore = ChartStore<UserState, UserAction>(
        initialStateRecord = initialState
    )

    // 3. Observe state changes
    val job = launch {
        chartStore.stateRecord.collect { state ->
            // Pattern match on sealed class
            val userStatus = when (val data = state.userData.value) {
                is LoadingState.Loading -> "Loading..."
                is LoadingState.Success -> "User: ${data.data}"
                is LoadingState.Error -> "Error: ${data.message}"
            }

            val errorStatus = state.lastError.value?.let { "Last error: $it" } ?: "No errors"

            println("$userStatus | $errorStatus")
        }
    }

    // 4. Simulate user data loading
    delay(100)
    println("\n--- Start loading ---")
    chartStore.dispatch(UserAction.LoadUser)

    delay(500)
    println("\n--- User loaded successfully ---")
    chartStore.dispatch(UserAction.UserLoaded("Alice"))

    delay(500)
    println("\n--- Load again ---")
    chartStore.dispatch(UserAction.LoadUser)

    delay(500)
    println("\n--- Loading failed ---")
    chartStore.dispatch(UserAction.UserLoadFailed("Network error"))

    delay(100)

    // 5. Clean up
    job.cancel()
    chartStore.close()
}

/**
 * Example output:
 *
 * Loading... | No errors
 *
 * --- Start loading ---
 * Loading... | No errors
 *
 * --- User loaded successfully ---
 * User: Alice | No errors
 *
 * --- Load again ---
 * Loading... | No errors
 *
 * --- Loading failed ---
 * Error: Network error | Last error: Network error
 */
