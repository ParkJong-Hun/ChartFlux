package example

import parkjonghun.github.io.chartflux.action.Action

/**
 * Actions for the counter example
 */
sealed class CounterAction : Action {
    data object Increment : CounterAction()
    data object Decrement : CounterAction()
    data class SetValue(val value: Int) : CounterAction()
}
