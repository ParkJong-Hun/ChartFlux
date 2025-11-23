package parkjonghun.github.io.chartflux.state.staterecord

import parkjonghun.github.io.chartflux.action.Action
import parkjonghun.github.io.chartflux.state.staterecord.stateproperty.StateProperty
import kotlin.reflect.KProperty1

interface UpdateContext<SR : StateRecord, A : Action> {
    val current: SR
    val action: A
    fun <T> compute(property: KProperty1<SR, StateProperty<SR, A, T, *>>): T
}

class CircularDependencyException(
    val dependencyChain: List<String>
) : RuntimeException("Circular dependency detected: ${dependencyChain.joinToString(" -> ")}")
