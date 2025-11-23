package example

import parkjonghun.github.io.chartflux.state.staterecord.UpdateContext
import parkjonghun.github.io.chartflux.state.staterecord.stateproperty.StateProperty

/**
 * CountProperty manages the counter value
 * Each StateProperty knows how to update itself in response to actions
 */
data class CountProperty(
    override val value: Int
) : StateProperty<CounterState, CounterAction, Int, CountProperty>() {

    override fun initial(): CountProperty {
        return CountProperty(0)
    }

    override fun spec(context: UpdateContext<CounterState, CounterAction>, action: CounterAction): CountProperty {
        return when (action) {
            is CounterAction.Increment -> CountProperty(value + 1)
            is CounterAction.Decrement -> CountProperty(value - 1)
            is CounterAction.SetValue -> CountProperty(action.value)
        }
    }
}

/**
 * MessageProperty manages the status message
 * It can access other properties' computed values through the context
 *
 * This demonstrates lazy evaluation: MessageProperty can request the updated
 * count value even if CountProperty hasn't been computed yet
 */
data class MessageProperty(
    override val value: String
) : StateProperty<CounterState, CounterAction, String, MessageProperty>() {

    override fun initial(): MessageProperty {
        return MessageProperty("Ready")
    }

    override fun spec(context: UpdateContext<CounterState, CounterAction>, action: CounterAction): MessageProperty {
        // Get the updated count value using lazy evaluation
        val newCount = context.compute(CounterState::count)

        return when (action) {
            is CounterAction.Increment ->
                MessageProperty("Incremented to $newCount")
            is CounterAction.Decrement ->
                MessageProperty("Decremented to $newCount")
            is CounterAction.SetValue ->
                MessageProperty("Set to $newCount")
        }
    }
}
