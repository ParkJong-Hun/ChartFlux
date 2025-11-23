package example

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import parkjonghun.github.io.chartflux.ChartStore

/**
 * Example usage of ChartFlux with the counter state
 *
 * This example demonstrates:
 * - Creating a ChartStore instance
 * - Observing state changes
 * - Dispatching actions
 * - Automatic property updates
 */
fun main() = runBlocking {
    // 1. Create initial state
    val initialState = CounterState(
        count = CountProperty(0),
        message = MessageProperty("Ready")
    )

    // 2. Create ChartStore instance
    val chartStore = ChartStore<CounterState, CounterAction>(
        initialStateRecord = initialState
    )

    // 3. Observe state changes
    val job = launch {
        chartStore.stateRecord.collect { state ->
            println("Count: ${state.count.value}, Message: ${state.message.value}")
        }
    }

    // 4. Dispatch actions
    delay(100)
    println("\n--- Increment ---")
    chartStore.dispatch(CounterAction.Increment)

    delay(100)
    println("\n--- Increment ---")
    chartStore.dispatch(CounterAction.Increment)

    delay(100)
    println("\n--- Decrement ---")
    chartStore.dispatch(CounterAction.Decrement)

    delay(100)
    println("\n--- Set to 100 ---")
    chartStore.dispatch(CounterAction.SetValue(100))

    delay(100)

    // 5. Clean up
    job.cancel()
    chartStore.close()
}

/**
 * Example output:
 *
 * Count: 0, Message: Ready
 *
 * --- Increment ---
 * Count: 1, Message: Incremented to 1
 *
 * --- Increment ---
 * Count: 2, Message: Incremented to 2
 *
 * --- Decrement ---
 * Count: 1, Message: Decremented to 1
 *
 * --- Set to 100 ---
 * Count: 100, Message: Set to 100
 */
