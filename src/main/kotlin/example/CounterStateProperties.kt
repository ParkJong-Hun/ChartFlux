package example

import parkjonghun.github.io.chartflux.staterecord.state.staterecord.stateproperty.StateProperty

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

    override fun spec(stateRecord: CounterState, action: CounterAction): CountProperty {
        return when (action) {
            is CounterAction.Increment -> CountProperty(value + 1)
            is CounterAction.Decrement -> CountProperty(value - 1)
            is CounterAction.SetValue -> CountProperty(action.value)
        }
    }
}

/**
 * MessageProperty manages the status message
 * It can access the full state to create contextual messages
 */
data class MessageProperty(
    override val value: String
) : StateProperty<CounterState, CounterAction, String, MessageProperty>() {

    override fun initial(): MessageProperty {
        return MessageProperty("Ready")
    }

    override fun spec(stateRecord: CounterState, action: CounterAction): MessageProperty {
        return when (action) {
            is CounterAction.Increment ->
                MessageProperty("Incremented to ${stateRecord.count.value + 1}")
            is CounterAction.Decrement ->
                MessageProperty("Decremented to ${stateRecord.count.value - 1}")
            is CounterAction.SetValue ->
                MessageProperty("Set to ${action.value}")
        }
    }
}
