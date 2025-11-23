package example.advanced

import parkjonghun.github.io.chartflux.action.Action
import parkjonghun.github.io.chartflux.state.staterecord.CircularDependencyException
import parkjonghun.github.io.chartflux.state.staterecord.StateRecord
import parkjonghun.github.io.chartflux.state.staterecord.UpdateContext
import parkjonghun.github.io.chartflux.state.staterecord.stateproperty.StateProperty
import parkjonghun.github.io.chartflux.state.staterecord.updateStateRecord

/**
 * Example demonstrating circular dependency detection
 *
 * This example intentionally creates a circular dependency to show
 * how the framework detects and reports it
 */

sealed class CircularAction : Action {
    data object TriggerCircular : CircularAction()
}

data class CircularState(
    val propertyA: PropertyA,
    val propertyB: PropertyB
) : StateRecord

/**
 * PropertyA depends on PropertyB
 */
data class PropertyA(
    override val value: Int
) : StateProperty<CircularState, CircularAction, Int, PropertyA>() {

    override fun initial(): PropertyA = PropertyA(0)

    override fun spec(
        context: UpdateContext<CircularState, CircularAction>,
        action: CircularAction
    ): PropertyA {
        return when (action) {
            is CircularAction.TriggerCircular -> {
                // This will cause a circular dependency!
                // PropertyA -> PropertyB -> PropertyA
                val b = context.compute(CircularState::propertyB)
                PropertyA(b + 1)
            }
        }
    }
}

/**
 * PropertyB depends on PropertyA - creating a circular dependency
 */
data class PropertyB(
    override val value: Int
) : StateProperty<CircularState, CircularAction, Int, PropertyB>() {

    override fun initial(): PropertyB = PropertyB(0)

    override fun spec(
        context: UpdateContext<CircularState, CircularAction>,
        action: CircularAction
    ): PropertyB {
        return when (action) {
            is CircularAction.TriggerCircular -> {
                // This depends on PropertyA, but PropertyA also depends on this!
                val a = context.compute(CircularState::propertyA)
                PropertyB(a + 1)
            }
        }
    }
}

/**
 * Example usage demonstrating circular dependency detection
 */
fun main() {
    val state = CircularState(
        propertyA = PropertyA(0),
        propertyB = PropertyB(0)
    )

    println("Attempting to trigger circular dependency...")
    println()

    try {
        updateStateRecord(state, CircularAction.TriggerCircular)
        println("ERROR: Should have thrown CircularDependencyException!")
    } catch (e: CircularDependencyException) {
        println("✓ Circular dependency detected successfully!")
        println("  Dependency chain: ${e.dependencyChain.joinToString(" -> ")}")
        println()
        println("This is the expected behavior. The framework detected that:")
        println("  - PropertyA tried to compute PropertyB")
        println("  - PropertyB tried to compute PropertyA")
        println("  - This would create an infinite loop")
        println()
        println("The error was caught and reported with the full dependency chain.")
    }
}
