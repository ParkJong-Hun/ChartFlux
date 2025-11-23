package example

import parkjonghun.github.io.chartflux.state.staterecord.StateRecord

/**
 * CounterState is a StateRecord that groups related StateProperties
 *
 * When an action is dispatched:
 * 1. Each property's spec() is called automatically
 * 2. A new CounterState is created with the updated properties
 *
 * No manual reducer needed!
 */
data class CounterState(
    val count: CountProperty,
    val message: MessageProperty
) : StateRecord
