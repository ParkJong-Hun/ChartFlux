package example.advanced

import parkjonghun.github.io.chartflux.action.Action
import parkjonghun.github.io.chartflux.state.staterecord.StateRecord
import parkjonghun.github.io.chartflux.state.staterecord.UpdateContext
import parkjonghun.github.io.chartflux.state.staterecord.stateproperty.StateProperty
import parkjonghun.github.io.chartflux.state.staterecord.updateStateRecord

/**
 * Example demonstrating mutual dependencies between properties
 *
 * This example shows how properties can depend on each other's updated values
 * using lazy evaluation through UpdateContext
 */

// Actions
sealed class CalculationAction : Action {
    data class SetValue(val value: Int) : CalculationAction()
    data object Calculate : CalculationAction()
}

// State
data class CalculationState(
    val input: InputProperty,
    val doubled: DoubledProperty,
    val tripled: TripledProperty,
    val summary: SummaryProperty
) : StateRecord

// Properties with mutual dependencies

/**
 * Basic input property
 */
data class InputProperty(
    override val value: Int
) : StateProperty<CalculationState, CalculationAction, Int, InputProperty>() {

    override fun initial(): InputProperty = InputProperty(0)

    override fun spec(
        context: UpdateContext<CalculationState, CalculationAction>,
        action: CalculationAction
    ): InputProperty {
        return when (action) {
            is CalculationAction.SetValue -> InputProperty(action.value)
            is CalculationAction.Calculate -> InputProperty(value)
        }
    }
}

/**
 * Doubled property - depends on input
 */
data class DoubledProperty(
    override val value: Int
) : StateProperty<CalculationState, CalculationAction, Int, DoubledProperty>() {

    override fun initial(): DoubledProperty = DoubledProperty(0)

    override fun spec(
        context: UpdateContext<CalculationState, CalculationAction>,
        action: CalculationAction
    ): DoubledProperty {
        return when (action) {
            is CalculationAction.Calculate -> {
                // Depends on the updated input value
                val input = context.compute(CalculationState::input)
                DoubledProperty(input * 2)
            }
            else -> DoubledProperty(value)
        }
    }
}

/**
 * Tripled property - depends on input
 */
data class TripledProperty(
    override val value: Int
) : StateProperty<CalculationState, CalculationAction, Int, TripledProperty>() {

    override fun initial(): TripledProperty = TripledProperty(0)

    override fun spec(
        context: UpdateContext<CalculationState, CalculationAction>,
        action: CalculationAction
    ): TripledProperty {
        return when (action) {
            is CalculationAction.Calculate -> {
                // Depends on the updated input value
                val input = context.compute(CalculationState::input)
                TripledProperty(input * 3)
            }
            else -> TripledProperty(value)
        }
    }
}

/**
 * Summary property - depends on multiple other properties
 *
 * This demonstrates a property that depends on several other properties,
 * showing how lazy evaluation allows flexible dependency chains
 */
data class SummaryProperty(
    override val value: String
) : StateProperty<CalculationState, CalculationAction, String, SummaryProperty>() {

    override fun initial(): SummaryProperty = SummaryProperty("No calculation yet")

    override fun spec(
        context: UpdateContext<CalculationState, CalculationAction>,
        action: CalculationAction
    ): SummaryProperty {
        return when (action) {
            is CalculationAction.Calculate -> {
                // Depends on all other computed properties
                val input = context.compute(CalculationState::input)
                val doubled = context.compute(CalculationState::doubled)
                val tripled = context.compute(CalculationState::tripled)

                SummaryProperty(
                    "Input: $input, Doubled: $doubled, Tripled: $tripled, Sum: ${input + doubled + tripled}"
                )
            }
            else -> SummaryProperty(value)
        }
    }
}

/**
 * Example usage demonstrating property dependencies
 */
fun main() {
    // Create initial state
    val initialState = CalculationState(
        input = InputProperty(0),
        doubled = DoubledProperty(0),
        tripled = TripledProperty(0),
        summary = SummaryProperty("No calculation yet")
    )

    println("Initial state:")
    println("  Input: ${initialState.input.value}")
    println("  Summary: ${initialState.summary.value}")
    println()

    // Set a value
    val stateAfterSet = updateStateRecord(
        initialState,
        CalculationAction.SetValue(5)
    ) as CalculationState

    println("After setting value to 5:")
    println("  Input: ${stateAfterSet.input.value}")
    println("  Summary: ${stateAfterSet.summary.value}")
    println()

    // Calculate - this will trigger all dependencies
    val stateAfterCalculate = updateStateRecord(
        stateAfterSet,
        CalculationAction.Calculate
    ) as CalculationState

    println("After calculate:")
    println("  Input: ${stateAfterCalculate.input.value}")
    println("  Doubled: ${stateAfterCalculate.doubled.value}")
    println("  Tripled: ${stateAfterCalculate.tripled.value}")
    println("  Summary: ${stateAfterCalculate.summary.value}")
    println()

    // The summary property automatically gets the updated values of all other properties
    // through lazy evaluation, even though they might not have been computed yet
    // when summary's spec function starts executing
}
